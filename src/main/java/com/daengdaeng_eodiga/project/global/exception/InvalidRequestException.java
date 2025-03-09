package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;


public class InvalidRequestException extends BusinessException {
	public InvalidRequestException(String entity, String details) {
		super(
			ErrorCode.INVALID_REQUEST.getErrorCode(),
			String.format("%s : %s - %s", entity, details, ErrorCode.INVALID_REQUEST.getMessage())
		);
	}
}

