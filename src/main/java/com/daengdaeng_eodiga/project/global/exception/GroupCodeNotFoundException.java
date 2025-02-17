package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class GroupCodeNotFoundException extends BusinessException {
	public GroupCodeNotFoundException() {
		super(ErrorCode.GROUP_CODE_NOT_FOUND.getErrorCode(), ErrorCode.GROUP_CODE_NOT_FOUND.getMessage());
	}
}