package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;
import com.daengdaeng_eodiga.project.global.exception.BusinessException;

public class UserNotFoundGeoAsAddress extends BusinessException {
	public UserNotFoundGeoAsAddress() {
		super(ErrorCode.USER_UNAUTHORIZED.getErrorCode(), ErrorCode.MISSING_PLACE_ADDRESS.getMessage());
	}
}