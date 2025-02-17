package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class UserUnauthorizedException extends BusinessException {
	public UserUnauthorizedException() {
		super(ErrorCode.USER_UNAUTHORIZED.getErrorCode(), ErrorCode.USER_UNAUTHORIZED.getMessage());
	}
}