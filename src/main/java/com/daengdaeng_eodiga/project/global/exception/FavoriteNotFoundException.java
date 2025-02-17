package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class FavoriteNotFoundException extends BusinessException {
	public FavoriteNotFoundException() {
		super(ErrorCode.FAVORITE_NOT_FOUND.getErrorCode(), ErrorCode.FAVORITE_NOT_FOUND.getMessage());
	}
}