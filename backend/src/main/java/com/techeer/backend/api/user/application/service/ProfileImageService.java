package com.techeer.backend.api.user.application.service;

import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.File;
import com.techeer.backend.api.user.domain.FileType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.infra.gcp.FileMetadata;
import com.techeer.backend.infra.gcp.FileTypeMapper;
import com.techeer.backend.infra.gcp.GcsUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileImageService {

	private final UserService userService;

	private final GcsUploader gcsUploader;

	private final FileTypeMapper fileTypeMapper;

	/**
	 * 프로필 이미지 업데이트 비즈니스 로직: 현재 로그인한 사용자의 프로필 이미지를 GCS에 업로드하고 File 객체로 저장
	 * 기존 프로필 이미지가 있으면 GCS에서 삭제
	 */
	@Transactional
	public String updateProfileImage(MultipartFile file) {
		User user = userService.getLoginUser();

		// 파일 타입 검증: 프로필 이미지는 이미지 파일만 허용
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			log.warn("프로필 이미지 업로드 실패: 이미지 파일이 아님. contentType={}", contentType);
			throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
		}

		// 파일 크기 검증 (10MB 제한 - 프로필 이미지용)
		long maxFileSize = 10 * 1024 * 1024; // 10MB
		if (file.getSize() > maxFileSize) {
			log.warn("프로필 이미지 업로드 실패: 파일 크기 초과. size={} bytes", file.getSize());
			throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
		}

		// 기존 프로필 이미지가 있으면 GCS에서 삭제
		if (user.getProfileImage() != null && user.getProfileImage().getFileUrl() != null
			&& !user.getProfileImage().getFileUrl().isEmpty()) {
			String oldImageUrl = user.getProfileImage().getFileUrl();
			String oldGcsPath = extractGcsPathFromUrl(oldImageUrl);
			if (oldGcsPath != null) {
				gcsUploader.deleteFile(oldGcsPath);
				log.info("기존 프로필 이미지 삭제 완료: gcsPath={}", oldGcsPath);
			}
		}

		// 새로운 프로필 이미지 업로드
		FileMetadata fileMetadata = gcsUploader.uploadProfileImage(file, user.getId());
		FileType fileType = fileTypeMapper.determineFileType(fileMetadata.getContentType());

		// File 객체 생성
		File profileImage = File.builder()
			.fileUrl(fileMetadata.getFileUrl())
			.fileType(fileType)
			.fileName(fileMetadata.getFileName())
			.fileUUID(fileMetadata.getFileUUID())
			.build();

		// 사용자 엔티티에 프로필 이미지 저장
		user.updateProfileImage(profileImage);
		// @Transactional 내에서 엔티티 수정 시 변경 감지로 자동 저장

		log.info("프로필 이미지 업데이트 완료: userId={}, profileImageUrl={}, fileType={}",
			user.getId(), fileMetadata.getFileUrl(), fileType);

		return fileMetadata.getFileUrl();
	}

	/**
	 * GCS URL에서 경로 추출
	 * 예: https://storage.googleapis.com/download/storage/v1/b/bucket/o/profile%2Ffilename.png?generation=...
	 * -> profile/filename.png
	 */
	private String extractGcsPathFromUrl(String url) {
		if (url == null || url.isEmpty()) {
			return null;
		}

		// URL에서 "o/" 다음 부분 추출
		int oIndex = url.indexOf("/o/");
		if (oIndex == -1) {
			return null;
		}

		String pathPart = url.substring(oIndex + 3);

		// 쿼리 파라미터 제거
		int queryIndex = pathPart.indexOf("?");
		if (queryIndex != -1) {
			pathPart = pathPart.substring(0, queryIndex);
		}

		// URL 디코딩 (%2F -> /)
		return java.net.URLDecoder.decode(pathPart, java.nio.charset.StandardCharsets.UTF_8);
	}

}
