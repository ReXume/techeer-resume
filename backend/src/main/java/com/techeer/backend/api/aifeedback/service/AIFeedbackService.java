package com.techeer.backend.api.aifeedback.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.aifeedback.dto.AIFeedbackResponse;
import com.techeer.backend.api.aifeedback.repository.AIFeedbackRepository;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.domain.ResumePdf;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class AIFeedbackService {

    private final AIFeedbackRepository aiFeedbackRepository;
    private final ResumeRepository resumeRepository;  // 이력서 데이터를 가져오기 위한 레포지토리 추가
    private final AmazonS3 amazonS3;
    private final OpenAIService openAiService;

    public AIFeedbackService(AIFeedbackRepository aiFeedbackRepository, ResumeRepository resumeRepository, AmazonS3 amazonS3, OpenAIService openAiService) {
        this.aiFeedbackRepository = aiFeedbackRepository;
        this.resumeRepository = resumeRepository;
        this.amazonS3 = amazonS3;
        this.openAiService = openAiService;
    }

    // PDF 파일을 S3에서 읽어온 뒤 텍스트로 변환하고, GPT로 피드백을 요청한 후, 결과를 저장하는 메서드
    @Transactional
    public AIFeedbackResponse generateAIFeedbackFromS3(Long resumeId) {
        // 1. 이력서 정보 데이터베이스에서 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        // 2. ResumePdf 객체에서 S3 버킷 이름과 키 가져오기
        ResumePdf resumePdf = resume.getResumePdf();
        if (resumePdf == null) {
            throw new BusinessException(ErrorCode.RESUME_PDF_NOT_FOUND);
        }

        String bucketName = resumePdf.getPdf().getPdfUrl(); // Assuming pdfUrl contains the bucket name
        String key = resumePdf.getPdf().getPdfUUID(); // Assuming pdfUUID contains the S3 key

        // 3. S3에서 PDF 파일 가져오기
        InputStream pdfInputStream = getPdfFileFromS3(bucketName, key);

        // 4. PDF 파���을 텍스트로 변환
        String resumeText = extractTextFromPdf(pdfInputStream);

        // 5. OpenAI GPT API 호출을 통한 피드백 생성
        String fullResponse;
        try {
            fullResponse = openAiService.getAIFeedback(resumeText);
        } catch (IOException e) {
            // IOException 발생 시 처리 로직
            throw new BusinessException(ErrorCode.OPENAI_SERVER_ERROR);
        }

        // 6. ai피드백에서 content만 추출
        String feedbackContent = extractContentFromOpenAIResponse(fullResponse);

        // 7. AIFeedback 엔티티 생성 및 저장
        AIFeedback aiFeedback = AIFeedback.builder()
                .resumeId(resumeId)
                .feedback(feedbackContent)  // content만 저장
                .build();

        AIFeedback savedFeedback = aiFeedbackRepository.save(aiFeedback);

        return AIFeedbackResponse.of(savedFeedback);
    }

    // S3에서 PDF 파일을 가져오는 메서드
    private InputStream getPdfFileFromS3(String bucketName, String key) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.RESUME_UPLOAD_ERROR);
        }
    }

    // PDF 파일을 텍스트로 변환하는 메서드
    private String extractTextFromPdf(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 피드백 응답에서 content만 추출
    private String extractContentFromOpenAIResponse(String fullResponse) {
        // Gson 또는 Jackson 등의 라이브러리로 JSON 파싱
        JsonObject jsonResponse = JsonParser.parseString(fullResponse).getAsJsonObject();
        return jsonResponse.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
    }
}
