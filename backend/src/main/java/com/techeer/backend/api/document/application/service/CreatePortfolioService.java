package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.CreatePortfolioUseCase;
import com.techeer.backend.api.document.application.port.out.SavePortfolioPort;
import com.techeer.backend.api.document.domain.Portfolio;
import com.techeer.backend.api.document.dto.request.PortfolioCreateRequest;
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
public class CreatePortfolioService implements CreatePortfolioUseCase {

    private final SavePortfolioPort savePortfolioPort;
    private final LoadUserPort loadUserPort;
    private final SaveUserFilePort saveUserFilePort;
    private final GcsUploader gcsUploader;

    @Override
    public Long createPortfolio(PortfolioCreateRequest request, MultipartFile file) {
        User user = loadUserPort.findById(request.userId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        FileMetadata metadata = gcsUploader.uploadDocument(file, user.getId(), "portfolio");

        UserFile userFile = UserFile.builder()
            .user(user)
            .category(FileCategory.PORTFOLIO)
            .uuid(metadata.getFileUUID() != null ? metadata.getFileUUID() : UUID.randomUUID().toString())
            .fileUrl(metadata.getFileUrl())
            .fileType(mapToFileType(file.getContentType()))
            .originalName(file.getOriginalFilename())
            .build();

        saveUserFilePort.saveUserFile(userFile);

        Portfolio portfolio = Portfolio.builder()
            .file(userFile)
            .title(request.title())
            .isDefault(request.isDefault())
            .build();

        return savePortfolioPort.savePortfolio(portfolio).getId();
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

