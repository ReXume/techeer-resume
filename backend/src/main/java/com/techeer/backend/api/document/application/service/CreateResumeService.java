package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.CreateResumeUseCase;
import com.techeer.backend.api.document.application.port.out.SaveResumePort;
import com.techeer.backend.api.document.domain.Resume;
import com.techeer.backend.api.document.dto.request.ResumeCreateRequest;
import com.techeer.backend.api.file.application.port.out.SaveUserFilePort;
import com.techeer.backend.api.file.domain.FileCategory;
import com.techeer.backend.api.file.domain.UserFile;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.FileType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.infra.gcp.FileMetadata;
import com.techeer.backend.infra.gcp.GcsUploader;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateResumeService implements CreateResumeUseCase {

    private final SaveResumePort saveResumePort;
    private final LoadUserPort loadUserPort;
    private final SaveUserFilePort saveUserFilePort;
    private final GcsUploader gcsUploader;

    @Override
    public Long createResume(ResumeCreateRequest request, MultipartFile file, Long userId) {
        User user = loadUserPort.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 1. 파일 업로드
        FileMetadata metadata = gcsUploader.uploadDocument(file, user.getId(), "resume");

        // 2. UserFile 생성
        UserFile userFile = UserFile.builder()
            .user(user)
            .category(FileCategory.RESUME)
            .uuid(metadata.getFileUUID() != null ? metadata.getFileUUID() : UUID.randomUUID().toString())
            .fileUrl(metadata.getFileUrl())
            .fileType(mapToFileType(file.getContentType()))
            .originalName(file.getOriginalFilename())
            .build();

        saveUserFilePort.saveUserFile(userFile);

        // 3. Resume 생성
        Resume resume = Resume.builder()
            .file(userFile)
            .title(request.title())
            .isDefault(request.isDefault())
            .build();

        return saveResumePort.saveResume(resume).getId();
    }

    private FileType mapToFileType(String contentType) {
        if (contentType == null) return FileType.OTHER;
        if (contentType.startsWith("image/")) return FileType.IMAGE;
        if (contentType.equals("application/pdf")) return FileType.PDF;
        if (contentType.contains("msword") || contentType.contains("wordprocessingml")) return FileType.WORD;
        if (contentType.contains("excel") || contentType.contains("spreadsheetml")) return FileType.EXCEL;
        return FileType.OTHER;
    }
}
