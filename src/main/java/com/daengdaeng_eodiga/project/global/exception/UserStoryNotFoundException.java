package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class UserStoryNotFoundException extends BusinessException {
	public UserStoryNotFoundException() {
		super(ErrorCode.USER_STORY_NOT_FOUND.getErrorCode(), ErrorCode.USER_STORY_NOT_FOUND.getMessage());
	}
}