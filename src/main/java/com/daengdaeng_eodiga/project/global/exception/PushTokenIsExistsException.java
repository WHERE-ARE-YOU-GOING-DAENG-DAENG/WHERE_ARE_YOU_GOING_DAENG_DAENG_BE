package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class PushTokenIsExistsException extends BusinessException {
	public PushTokenIsExistsException() {
		super(ErrorCode.PUSH_TOKEN_IS_EXISTS.getErrorCode(), ErrorCode.PUSH_TOKEN_IS_EXISTS.getMessage());
	}
}