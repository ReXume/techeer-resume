package com.techeer.backend.api.aifeedback.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.aifeedback.repository.AIFeedbackRepository;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.domain.ResumePdf;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.infra.aws.S3Uploader;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(AIFeedbackService.class);

    private final AIFeedbackRepository aiFeedbackRepository;
    private final ResumeRepository resumeRepository;
    private final S3Uploader s3Uploader;
    private final OpenAIService openAiService;

    @Transactional
    public AIFeedback generateAIFeedbackFromS3(Long resumeId) {
        // 1. 이력서 정보 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        ResumePdf resumePdf = resume.getResumePdf();
        if (resumePdf == null) {
            throw new BusinessException(ErrorCode.RESUME_PDF_NOT_FOUND);
        }

        // 2. S3에서 PDF 파일 가져오기
        String pdfUrl = resumePdf.getPdf().getPdfUrl();
        String s3Key = extractKeyFromUrl(pdfUrl);

        // 변경된 부분: S3Uploader에서 직접 bucket name을 받아 사용
        InputStream pdfInputStream = s3Uploader.getFileFromS3(s3Uploader.getBucketName(), s3Key);

        // PDF 텍스트 추출
        String resumeText = extractTextFromPdf(pdfInputStream);

        // 3. OpenAI API로 피드백 생성
        String fullResponse;
        try {
            fullResponse = openAiService.getAIFeedback(resumeText);
        } catch (IOException e) {
            log.error("OpenAI API 호출 중 에러가 발생했습니다.", e);
            throw new BusinessException(ErrorCode.OPENAI_SERVER_ERROR);
        }
        String feedbackContent = extractContentFromOpenAIResponse(fullResponse);

        // 4. AIFeedback 엔티티 생성 및 저장
        AIFeedback aiFeedback = AIFeedback.builder()
                .resumeId(resumeId)
                .feedback(feedbackContent)
                .build();

        return aiFeedbackRepository.save(aiFeedback);
    }

    /**
     * AIFeedback 조회
     */
    public AIFeedback getFeedbackById(Long aifeedbackId) {
        return aiFeedbackRepository.findById(aifeedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));
    }

    /**
     * URL에서 S3 Key 추출
     */
    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            // 첫 '/' 제거
            String key = url.getPath().substring(1);
            return URLDecoder.decode(key, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("Error extracting S3 key from URL: {}", fileUrl, e);
            throw new BusinessException(ErrorCode.RESUME_UPLOAD_ERROR);
        }
    }

    /**
     * PDF -> 텍스트 변환
     */
    private String extractTextFromPdf(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            log.error("Error extracting text from PDF", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * OpenAI API 응답에서 content만 추출
     */
    private String extractContentFromOpenAIResponse(String fullResponse) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(fullResponse).getAsJsonObject();
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        } catch (Exception e) {
            log.error("Error parsing OpenAI response", e);
            throw new BusinessException(ErrorCode.OPENAI_RESPONSE_PARSE_ERROR);
        }
    }
}
