package com.techeer.backend.api.aifeedback.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.aifeedback.repository.AIFeedbackRepository;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.domain.ResumePdf;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(AIFeedbackService.class);

    private final AIFeedbackRepository aiFeedbackRepository;
    private final ResumeRepository resumeRepository;  // 이력서 데이터를 가져오기 위한 레포지토리 추가
    private final AmazonS3 amazonS3;
    private final OpenAIService openAiService;

    public AIFeedbackService(AIFeedbackRepository aiFeedbackRepository, ResumeRepository resumeRepository,
                             AmazonS3 amazonS3, OpenAIService openAiService) {
        this.aiFeedbackRepository = aiFeedbackRepository;
        this.resumeRepository = resumeRepository;
        this.amazonS3 = amazonS3;
        this.openAiService = openAiService;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public AIFeedback generateAIFeedbackFromS3(Long resumeId) {
        // 1. 이력서 정보 조회 및 S3에서 PDF 읽기, 텍스트 변환 등 기존 로직 수행
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));
        ResumePdf resumePdf = resume.getResumePdf();
        if (resumePdf == null) {
            throw new BusinessException(ErrorCode.RESUME_PDF_NOT_FOUND);
        }

        // S3 key 추출, PDF 텍스트 변환, OpenAI API 호출 등 기존 로직...
        String resumeText = extractTextFromPdf(
                getPdfFileFromS3(bucket, extractKeyFromUrl(resumePdf.getPdf().getPdfUrl())));
        String fullResponse;
        try {
            fullResponse = openAiService.getAIFeedback(resumeText);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPENAI_SERVER_ERROR);
        }
        String feedbackContent = extractContentFromOpenAIResponse(fullResponse);

        // 2. 도메인 객체(AIFeedback) 생성 및 저장
        AIFeedback aiFeedback = AIFeedback.builder()
                .resumeId(resumeId)
                .feedback(feedbackContent)
                .build();

        return aiFeedbackRepository.save(aiFeedback);
    }

    public AIFeedback getFeedbackById(Long aifeedbackId) {
        return aiFeedbackRepository.findById(aifeedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));
    }

    // 별도의 메서드로 URL에서 key 추출 (예시)
    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String key = url.getPath().substring(1); // 선행 '/' 제거
            return URLDecoder.decode(key, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.RESUME_UPLOAD_ERROR);
        }
    }

    // S3에서 PDF 파일을 가져오는 메서드 (로그 추가)
    private InputStream getPdfFileFromS3(String bucketName, String key) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            log.info("Successfully retrieved S3 object. Content length: {}",
                    s3Object.getObjectMetadata().getContentLength());
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Error retrieving PDF from S3. Bucket: '{}', Key: '{}'", bucketName, key, e);
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
