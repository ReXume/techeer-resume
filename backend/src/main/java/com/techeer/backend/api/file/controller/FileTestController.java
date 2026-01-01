package com.techeer.backend.api.file.controller;

import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import com.techeer.backend.infra.gcp.FileMetadata;
import com.techeer.backend.infra.gcp.GcsUploader;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/files")
public class FileTestController {

	private final GcsUploader gcsUploader;

	private final UserService userService;

	@Operation(summary = "프로필 이미지 업로드 테스트", description = "현재 로그인한 사용자의 프로필 이미지를 업로드합니다.")
	@PostMapping("/profile")
	public ResponseEntity<ApiResponse<String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
		User user = userService.getLoginUser();
		FileMetadata fileMetadata = gcsUploader.uploadProfileImage(file, user.getId());

		log.info("프로필 이미지 업로드 성공: userId={}, fileUrl={}", user.getId(), fileMetadata.getFileUrl());

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.FILE_UPLOAD_SUCCESS, fileMetadata.getFileUrl()));
	}

	@Operation(summary = "문서 파일 업로드 테스트", description = "현재 로그인한 사용자의 문서 파일(이력서, 포트폴리오 등)을 업로드합니다.")
	@PostMapping("/document")
	public ResponseEntity<ApiResponse<String>> uploadDocument(@RequestParam("file") MultipartFile file,
															  @RequestParam(value = "type", defaultValue = "resume") String documentType) {
		User user = userService.getLoginUser();
		FileMetadata fileMetadata = gcsUploader.uploadDocument(file, user.getId(), documentType);

		log.info("문서 파일 업로드 성공: userId={}, documentType={}, fileUrl={}", user.getId(), documentType,
			fileMetadata.getFileUrl());

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.FILE_UPLOAD_SUCCESS, fileMetadata.getFileUrl()));
	}

	@Operation(summary = "증명 파일 업로드 테스트", description = "현재 로그인한 사용자의 증명 파일(학력, 자격증 등)을 업로드합니다.")
	@PostMapping("/verification")
	public ResponseEntity<ApiResponse<String>> uploadVerification(@RequestParam("file") MultipartFile file,
																  @RequestParam(value = "type", defaultValue = "education") String verificationType) {
		User user = userService.getLoginUser();
		FileMetadata fileMetadata = gcsUploader.uploadVerification(file, user.getId(), verificationType);

		log.info("증명 파일 업로드 성공: userId={}, verificationType={}, fileUrl={}", user.getId(), verificationType,
			fileMetadata.getFileUrl());

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.FILE_UPLOAD_SUCCESS, fileMetadata.getFileUrl()));
	}

	@Operation(summary = "파일 다운로드 테스트", description = "GCS에서 파일을 다운로드합니다. gcsPath는 'profile/filename.jpg' 형식입니다.")
	@GetMapping("/download")
	public void downloadFile(@RequestParam("gcsPath") String gcsPath, HttpServletResponse response) throws IOException {
		try (var inputStream = gcsUploader.downloadFile(gcsPath)) {
			// 파일명 추출
			String fileName = gcsPath.substring(gcsPath.lastIndexOf("/") + 1);

			// Content-Type 설정
			String contentType = determineContentType(fileName);
			response.setContentType(contentType);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

			// 파일 스트림 복사
			inputStream.transferTo(response.getOutputStream());
			response.getOutputStream().flush();

			log.info("파일 다운로드 성공: gcsPath={}", gcsPath);
		}
	}

	@Operation(summary = "파일 삭제 테스트", description = "GCS에서 파일을 삭제합니다. gcsPath는 'profile/filename.jpg' 형식입니다.")
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam("gcsPath") String gcsPath) {
		gcsUploader.deleteFile(gcsPath);

		log.info("파일 삭제 성공: gcsPath={}", gcsPath);

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.FILE_DELETE_SUCCESS));
	}

	@Operation(summary = "버킷 정보 조회", description = "현재 사용 중인 GCS 버킷 이름을 조회합니다.")
	@GetMapping("/bucket")
	public ResponseEntity<ApiResponse<String>> getBucketName() {
		String bucketName = gcsUploader.getBucketName();

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, bucketName));
	}

	/**
	 * 파일명으로 Content-Type 결정
	 */
	private String determineContentType(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		return switch (extension) {
			case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
			case "png" -> MediaType.IMAGE_PNG_VALUE;
			case "gif" -> MediaType.IMAGE_GIF_VALUE;
			case "pdf" -> MediaType.APPLICATION_PDF_VALUE;
			default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
		};
	}

}
