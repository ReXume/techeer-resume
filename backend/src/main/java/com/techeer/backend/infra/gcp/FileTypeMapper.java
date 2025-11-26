package com.techeer.backend.infra.gcp;

import com.techeer.backend.api.user.domain.FileType;
import org.springframework.stereotype.Component;

@Component
public class FileTypeMapper {

    /**
     * ContentType에서 FileType 결정
     *
     * @param contentType Content-Type (예: "image/png", "application/pdf")
     * @return FileType
     */
    public FileType determineFileType(String contentType) {
        if (contentType == null) {
            return FileType.OTHER;
        }

        if (contentType.startsWith("image/")) {
            return FileType.IMAGE;
        } else if (contentType.equals("application/pdf")) {
            return FileType.PDF;
        } else if (contentType.contains("word") || contentType.contains("msword") || 
                   contentType.contains("document")) {
            return FileType.WORD;
        } else if (contentType.contains("excel") || contentType.contains("spreadsheet")) {
            return FileType.EXCEL;
        } else if (contentType.contains("google") || contentType.contains("docs")) {
            return FileType.GOOGLE_DOCS;
        } else {
            return FileType.OTHER;
        }
    }
}

