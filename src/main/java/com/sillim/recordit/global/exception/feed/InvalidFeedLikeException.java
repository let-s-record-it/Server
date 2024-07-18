package com.sillim.recordit.global.exception.feed;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidFeedLikeException extends ApplicationException {
	public InvalidFeedLikeException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidFeedLikeException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
