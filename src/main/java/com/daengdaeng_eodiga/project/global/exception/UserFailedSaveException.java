package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class UserFailedSaveException extends BusinessException {
    public UserFailedSaveException() {
        super(ErrorCode.USER_FAILED_SAVE.getErrorCode(), ErrorCode.USER_FAILED_SAVE.getMessage());
    }
}
