package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class DuplicateUserException extends BusinessException {
    public DuplicateUserException() {
        super(ErrorCode.DUPLICATE_USER.getErrorCode(), ErrorCode.DUPLICATE_USER.getMessage());
    }
}