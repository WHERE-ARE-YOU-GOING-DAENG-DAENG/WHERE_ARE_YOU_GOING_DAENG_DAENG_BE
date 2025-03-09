package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class OpeningDateNotFoundException extends BusinessException {
	public OpeningDateNotFoundException() {
		super(ErrorCode.OPENING_DATE_NOT_FOUND.getErrorCode(), ErrorCode.OPENING_DATE_NOT_FOUND.getMessage());
	}
}