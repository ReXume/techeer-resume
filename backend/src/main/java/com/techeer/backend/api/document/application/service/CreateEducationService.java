package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.CreateEducationUseCase;
import com.techeer.backend.api.document.application.port.out.SaveEducationPort;
import com.techeer.backend.api.document.domain.Education;
import com.techeer.backend.api.document.dto.request.EducationCreateRequest;
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
public class CreateEducationService implements CreateEducationUseCase {

    private final SaveEducationPort saveEducationPort;

    private final LoadUserPort loadUserPort;

    private final SaveUserFilePort saveUserFilePort;

    private final GcsUploader gcsUploader;

    @Override
    public Long createEducation(EducationCreateRequest request, MultipartFile file, Long userId) {
        User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        FileMetadata metadata = gcsUploader.uploadVerification(file, user.getId(), "education");

        UserFile userFile = UserFile.builder()
                .user(user)
                .category(FileCategory.EDUCATION)
                .uuid(metadata.getFileUUID() != null ? metadata.getFileUUID() : UUID.randomUUID().toString())
                .fileUrl(metadata.getFileUrl())
                .fileType(mapToFileType(file.getContentType()))
                .originalName(file.getOriginalFilename())
                .build();

        saveUserFilePort.saveUserFile(userFile);

        Education education = Education.builder()
                .file(userFile)
                .title(request.title())
                .isDefault(request.isDefault())
                .build();

        return saveEducationPort.saveEducation(education).getId();
    }

    private FileType mapToFileType(String contentType) {
        if (contentType == null) {
            return FileType.OTHER;
        }
        if (contentType.startsWith("image/")) {
            return FileType.IMAGE;
        }
        if (contentType.equals("application/pdf")) {
            return FileType.PDF;
        }
        if (contentType.contains("msword") || contentType.contains("wordprocessingml")) {
            return FileType.WORD;
        }
        if (contentType.contains("excel") || contentType.contains("spreadsheetml")) {
            return FileType.EXCEL;
        }
        return FileType.OTHER;
    }

}
