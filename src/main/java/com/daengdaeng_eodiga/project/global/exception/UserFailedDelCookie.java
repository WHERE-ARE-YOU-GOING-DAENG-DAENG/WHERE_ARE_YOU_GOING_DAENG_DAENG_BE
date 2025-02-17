package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class UserFailedDelCookie extends BusinessException {
	public UserFailedDelCookie() {
		super(ErrorCode.COOKIE_DELETION_FAILED.getErrorCode(), ErrorCode.COOKIE_DELETION_FAILED.getMessage());
	}
}