package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class DayTypeNotFoundException extends BusinessException {
	public DayTypeNotFoundException() {
		super(ErrorCode.DAY_TYPE_NOT_FOUND.getErrorCode(), ErrorCode.DAY_TYPE_NOT_FOUND.getMessage());
	}
}