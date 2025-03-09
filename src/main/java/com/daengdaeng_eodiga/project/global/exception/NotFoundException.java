package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

//TODO : NotFoundException으로 통합
public class NotFoundException extends BusinessException {
	public NotFoundException(String entity,String detail) {
		super(ErrorCode.NOT_FOUND.getErrorCode(), 	String.format("%s : %s - %s", entity, detail, ErrorCode.NOT_FOUND.getMessage()));
	}
}