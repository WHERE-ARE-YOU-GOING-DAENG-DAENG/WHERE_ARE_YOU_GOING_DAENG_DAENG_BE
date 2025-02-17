package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class ReviewSummaryNotFoundException extends BusinessException {
  public ReviewSummaryNotFoundException() {
    super(ErrorCode.REVIEW_SUMMARY_NOT_FOUND.getErrorCode(), ErrorCode.REVIEW_SUMMARY_NOT_FOUND.getMessage());
  }
}
