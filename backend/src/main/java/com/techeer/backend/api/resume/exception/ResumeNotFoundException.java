package com.techeer.backend.api.resume.exception;

import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;

public class ResumeNotFoundException extends BusinessException {

    public ResumeNotFoundException() {
        super(ErrorCode.RESUME_NOT_FOUND);
    }
}
