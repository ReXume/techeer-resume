package com.techeer.backend.api.resume.service;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.domain.ResumePdf;
import com.techeer.backend.api.resume.repository.ResumePdfRepository;
import com.techeer.backend.global.vo.Pdf;
import com.techeer.backend.infra.aws.S3Uploader;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumePdfService {

    private final S3Uploader s3Uploader;
    private final ResumePdfRepository resumePdfRepository;


    public ResumePdf saveResumePdf(Resume resume, MultipartFile multipartFile) {
        String pdfUrl = s3Uploader.uploadPdf(multipartFile);
        Pdf pdf = Pdf.builder()
                .pdfUrl(pdfUrl)
                .pdfName(multipartFile.getOriginalFilename())
                .pdfUUID(UUID.randomUUID().toString())
                .build();

        ResumePdf resumePdf = ResumePdf.builder()
                .resume(resume)
                .pdf(pdf)
                .build();

        return resumePdfRepository.save(resumePdf);
    }

    public void deleteResumePdf(String fileName) {
        s3Uploader.delete(fileName);
    }

    public void deleteResumePdf(Long resumeId) {
        resumePdfRepository.findByResumeId(resumeId)
                .ifPresent(resumePdf -> s3Uploader.delete(resumePdf.getPdf().getPdfName()));
    }

}
