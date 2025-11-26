package com.techeer.backend.infra.gcp;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileMetadata {

	private String fileUrl;

	private String fileName;

	private String fileUUID;

	private String contentType;

}
