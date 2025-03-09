package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class DuplicatePreferenceException extends BusinessException {
	public DuplicatePreferenceException() {
		super(ErrorCode.DUPLICATE_PREFERENCE.getErrorCode(), ErrorCode.DUPLICATE_PREFERENCE.getMessage());
	}
}
