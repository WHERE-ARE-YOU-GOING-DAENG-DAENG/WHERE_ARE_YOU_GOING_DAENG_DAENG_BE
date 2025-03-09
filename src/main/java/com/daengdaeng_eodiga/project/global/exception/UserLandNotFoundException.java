package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class UserLandNotFoundException extends BusinessException {
	public UserLandNotFoundException() {
		super(ErrorCode.USER_LAND_NOT_FOUND.getErrorCode(), ErrorCode.USER_LAND_NOT_FOUND.getMessage());
	}
}