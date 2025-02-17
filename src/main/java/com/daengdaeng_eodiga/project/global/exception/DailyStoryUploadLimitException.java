package com.daengdaeng_eodiga.project.global.exception;

import com.daengdaeng_eodiga.project.global.enums.ErrorCode;

public class DailyStoryUploadLimitException extends BusinessException {
	public DailyStoryUploadLimitException() {
		super(
			ErrorCode.DAILY_STORY_UPLOAD_LIMIT_EXCEEDED.getErrorCode(),
			ErrorCode.DAILY_STORY_UPLOAD_LIMIT_EXCEEDED.getMessage()
		);
	}
}