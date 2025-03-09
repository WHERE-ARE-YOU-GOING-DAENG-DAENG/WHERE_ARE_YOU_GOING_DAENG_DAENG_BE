package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class DuplicateReviewException extends BusinessException {
    public DuplicateReviewException() {
        super(ErrorCode.DUPLICATE_REVIEW_DAY.getErrorCode(), ErrorCode.DUPLICATE_REVIEW_DAY.getMessage());
    }
}