package com.techeer.backend.infra.gcp;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class GcsUploader {

    private final Storage storage;

    @Value("${gcp.gcs.bucket}")
    private String bucket;

    @Value("${gcp.gcs.folders.profile}")
    private String profileFolder;

    @Value("${gcp.gcs.folders.document}")
    private String documentFolder;

    @Value("${gcp.gcs.folders.verification}")
    private String verificationFolder;

    /**
     * 프로필 이미지 업로드
     *
     * @param multipartFile 업로드할 파일
     * @param userId 사용자 ID
     * @return 파일 메타데이터
     */
    public FileMetadata uploadProfileImage(MultipartFile multipartFile, Long userId) {
        validateFile(multipartFile);
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(multipartFile);
        String fileName = generateFileName(userId, "profile", extension);
        String gcsPath = profileFolder + "/" + fileName;
        String fileUrl = uploadToGcs(multipartFile, gcsPath);
        String fileUUID = extractUUIDFromFileName(fileName);
        
        return FileMetadata.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName != null ? originalFileName : fileName)
                .fileUUID(fileUUID)
                .contentType(multipartFile.getContentType())
                .build();
    }

    /**
     * 문서 파일 업로드 (이력서, 포트폴리오 등)
     *
     * @param multipartFile 업로드할 파일
     * @param userId 사용자 ID
     * @param documentType 문서 타입 (예: "resume", "portfolio")
     * @return 파일 메타데이터
     */
    public FileMetadata uploadDocument(MultipartFile multipartFile, Long userId, String documentType) {
        validateFile(multipartFile);
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(multipartFile);
        String fileName = generateFileName(userId, documentType, extension);
        String gcsPath = documentFolder + "/" + fileName;
        String fileUrl = uploadToGcs(multipartFile, gcsPath);
        String fileUUID = extractUUIDFromFileName(fileName);
        
        return FileMetadata.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName != null ? originalFileName : fileName)
                .fileUUID(fileUUID)
                .contentType(multipartFile.getContentType())
                .build();
    }

    /**
     * 증명 파일 업로드 (학력, 자격증 등)
     *
     * @param multipartFile 업로드할 파일
     * @param userId 사용자 ID
     * @param verificationType 증명 타입 (예: "education", "certificate")
     * @return 파일 메타데이터
     */
    public FileMetadata uploadVerification(MultipartFile multipartFile, Long userId, String verificationType) {
        validateFile(multipartFile);
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(multipartFile);
        String fileName = generateFileName(userId, verificationType, extension);
        String gcsPath = verificationFolder + "/" + fileName;
        String fileUrl = uploadToGcs(multipartFile, gcsPath);
        String fileUUID = extractUUIDFromFileName(fileName);
        
        return FileMetadata.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName != null ? originalFileName : fileName)
                .fileUUID(fileUUID)
                .contentType(multipartFile.getContentType())
                .build();
    }

    /**
     * GCS에 파일 업로드
     *
     * @param multipartFile 업로드할 파일
     * @param gcsPath GCS 내부 경로 (예: "profile/user-1-profile.jpg")
     * @return 업로드된 파일의 GCS URL
     */
    private String uploadToGcs(MultipartFile multipartFile, String gcsPath) {
        try {
            BlobId blobId = BlobId.of(bucket, gcsPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(multipartFile.getContentType())
                .build();

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Blob blob = storage.create(blobInfo, inputStream.readAllBytes());
                String fileUrl = blob.getMediaLink();
                log.info("파일 업로드 성공: bucket={}, path={}, url={}", bucket, gcsPath, fileUrl);
                return fileUrl;
            }
        } catch (IOException e) {
            log.error("파일 업로드 중 IOException 발생: path={}", gcsPath, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        } catch (StorageException e) {
            log.error("파일 업로드 중 StorageException 발생: path={}, code={}", gcsPath, e.getCode(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    /**
     * GCS에서 파일 삭제
     *
     * @param gcsPath GCS 내부 경로
     */
    public void deleteFile(String gcsPath) {
        try {
            BlobId blobId = BlobId.of(bucket, gcsPath);
            boolean deleted = storage.delete(blobId);
            
            if (deleted) {
                log.info("파일 삭제 성공: bucket={}, path={}", bucket, gcsPath);
            } else {
                log.warn("파일 삭제 실패 (파일이 존재하지 않음): bucket={}, path={}", bucket, gcsPath);
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }
        } catch (StorageException e) {
            log.error("파일 삭제 중 StorageException 발생: path={}, code={}", gcsPath, e.getCode(), e);
            throw new BusinessException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    /**
     * GCS에서 파일 다운로드
     *
     * @param gcsPath GCS 내부 경로
     * @return 파일 InputStream
     */
    public InputStream downloadFile(String gcsPath) {
        try {
            BlobId blobId = BlobId.of(bucket, gcsPath);
            Blob blob = storage.get(blobId);
            
            if (blob == null) {
                log.warn("파일을 찾을 수 없음: bucket={}, path={}", bucket, gcsPath);
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }
            
            log.info("파일 다운로드 성공: bucket={}, path={}, size={}", bucket, gcsPath, blob.getSize());
            return new ByteArrayInputStream(blob.getContent());
        } catch (StorageException e) {
            log.error("파일 다운로드 중 StorageException 발생: path={}, code={}", gcsPath, e.getCode(), e);
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    /**
     * 파일명 생성
     *
     * @param userId 사용자 ID
     * @param fileType 파일 타입
     * @param extension 파일 확장자
     * @return 생성된 파일명
     */
    private String generateFileName(Long userId, String fileType, String extension) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("user-%d-%s-%s%s", userId, fileType, uuid, extension);
    }

    /**
     * 파일명에서 UUID 추출
     *
     * @param fileName 파일명 (예: "user-1-profile-abc12345.jpg")
     * @return UUID (예: "abc12345")
     */
    private String extractUUIDFromFileName(String fileName) {
        // 파일명 형식: "user-{userId}-{type}-{uuid}{extension}"
        // UUID는 마지막 하이픈과 확장자 사이에 있음
        int lastHyphenIndex = fileName.lastIndexOf("-");
        int extensionIndex = fileName.lastIndexOf(".");
        
        if (lastHyphenIndex != -1 && extensionIndex > lastHyphenIndex) {
            return fileName.substring(lastHyphenIndex + 1, extensionIndex);
        }
        // UUID 추출 실패 시 전체 파일명 반환
        return fileName;
    }

    /**
     * 파일 확장자 추출
     *
     * @param multipartFile 파일
     * @return 확장자 (예: ".jpg", ".pdf")
     */
    private String getFileExtension(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    /**
     * 파일 유효성 검증
     *
     * @param multipartFile 검증할 파일
     */
    private void validateFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 파일 크기 검증 (100MB 제한)
        long maxFileSize = 100 * 1024 * 1024; // 100MB
        if (multipartFile.getSize() > maxFileSize) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // 파일 타입 검증 (선택적)
        String contentType = multipartFile.getContentType();
        if (contentType != null && !isAllowedContentType(contentType)) {
            log.warn("지원하지 않는 파일 타입: {}", contentType);
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    /**
     * 허용된 Content-Type 확인
     *
     * @param contentType Content-Type
     * @return 허용 여부
     */
    private boolean isAllowedContentType(String contentType) {
        return contentType.startsWith("image/") ||
               contentType.equals("application/pdf") ||
               contentType.startsWith("application/");
    }

    /**
     * 버킷 이름 반환
     *
     * @return 버킷 이름
     */
    public String getBucketName() {
        return bucket;
    }
}

