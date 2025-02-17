package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class BannerNotFoundException extends BusinessException {
	public BannerNotFoundException() {
		super(ErrorCode.BANNER_NOT_FOUND.getErrorCode(), ErrorCode.BANNER_NOT_FOUND.getMessage());
	}
}