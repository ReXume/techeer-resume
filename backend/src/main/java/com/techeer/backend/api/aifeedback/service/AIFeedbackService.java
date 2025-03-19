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
    private final ResumeRepository resumeRepository;
    private final AmazonS3 amazonS3;
    private final OpenAIService openAiService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public AIFeedbackService(AIFeedbackRepository aiFeedbackRepository, ResumeRepository resumeRepository,
                             AmazonS3 amazonS3, OpenAIService openAiService) {
        this.aiFeedbackRepository = aiFeedbackRepository;
        this.resumeRepository = resumeRepository;
        this.amazonS3 = amazonS3;
        this.openAiService = openAiService;
    }

    @Transactional
    public AIFeedback generateAIFeedbackFromS3(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        ResumePdf resumePdf = resume.getResumePdf();
        if (resumePdf == null) {
            throw new BusinessException(ErrorCode.RESUME_PDF_NOT_FOUND);
        }

        String resumeText = extractTextFromPdf(
                getPdfFileFromS3(bucket, extractKeyFromUrl(resumePdf.getPdf().getPdfUrl())));

        String fullResponse;
        try {
            fullResponse = openAiService.getAIFeedback(resumeText);
        } catch (IOException e) {
            log.error("OpenAI API 호출 중 에러가 발생했습니다.", e);
            throw new BusinessException(ErrorCode.OPENAI_SERVER_ERROR);
        }

        String feedbackContent = extractContentFromOpenAIResponse(fullResponse);

        AIFeedback aiFeedback = AIFeedback.builder()
                .resumeId(resumeId)
                .feedback(feedbackContent)
                .build();

        return aiFeedbackRepository.save(aiFeedback);
    }

    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String key = url.getPath().substring(1);
            return URLDecoder.decode(key, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.error("Error extracting S3 key from URL: {}", fileUrl, e);
            throw new BusinessException(ErrorCode.RESUME_UPLOAD_ERROR);
        }
    }

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

    private String extractTextFromPdf(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            log.error("Error extracting text from PDF", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

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

    public AIFeedback getFeedbackById(Long aifeedbackId) {
        return aiFeedbackRepository.findById(aifeedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AIFEEDBACK_NOT_FOUND));
    }

}