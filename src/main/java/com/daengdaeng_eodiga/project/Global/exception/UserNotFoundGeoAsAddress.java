package com.daengdaeng_eodiga.project.Global.exception;

import com.daengdaeng_eodiga.project.Global.enums.ErrorCode;
import com.daengdaeng_eodiga.project.Global.exception.BusinessException;

public class UserNotFoundGeoAsAddress extends BusinessException {
	public UserNotFoundGeoAsAddress() {
		super(ErrorCode.USER_UNAUTHORIZED.getErrorCode(), ErrorCode.MISSING_PLACE_ADDRESS.getMessage());
	}
}