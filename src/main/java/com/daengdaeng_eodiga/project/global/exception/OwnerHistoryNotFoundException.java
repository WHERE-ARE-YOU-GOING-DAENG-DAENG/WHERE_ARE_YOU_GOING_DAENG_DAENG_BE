package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class OwnerHistoryNotFoundException extends BusinessException {
	public OwnerHistoryNotFoundException() {
		super(ErrorCode.OWNER_HISTORY_NOT_FOUND.getErrorCode(), ErrorCode.OWNER_HISTORY_NOT_FOUND.getMessage());
	}
}