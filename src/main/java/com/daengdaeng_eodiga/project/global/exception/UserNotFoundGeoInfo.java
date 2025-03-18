package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class UserNotFoundGeoInfo extends BusinessException {
	public UserNotFoundGeoInfo() {
		super(ErrorCode.USER_UNAUTHORIZED.getErrorCode(), ErrorCode.MISSING_COORDINATES.getMessage());
	}
}