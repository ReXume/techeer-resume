package com.techeer.backend.api.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private FileType fileType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_uuid")
    private String fileUUID;

    /**
     * 파일 URL 업데이트
     */
    public void updateUrl(String url) {
        this.fileUrl = url;
    }

    /**
     * 파일 정보 업데이트
     */
    public void update(String url, FileType type, String name, String uuid) {
        this.fileUrl = url;
        this.fileType = type;
        this.fileName = name;
        this.fileUUID = uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFileUUID());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        File file = (File) obj;
        return Objects.equals(getFileUUID(), file.getFileUUID());
    }
}

