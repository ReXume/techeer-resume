package com.techeer.backend.api.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {

    IMAGE("이미지"), PDF("PDF 문서"), GOOGLE_DOCS("Google 문서"), WORD("Word 문서"), EXCEL("Excel 문서"), OTHER("기타 파일");

    private final String description;

}
