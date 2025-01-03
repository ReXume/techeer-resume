package com.techeer.backend.global.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pdf {

    private String pdfUrl;
    private String pdfName;
    private String pdfUUID;

    public void setUrl(String url) {
        this.pdfUrl = url;
    }

    @Override
    public int hashCode() {
        return pdfUUID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Pdf pdf = (Pdf) obj;
        return pdfUUID.equals(pdf.pdfUUID);
    }
}
