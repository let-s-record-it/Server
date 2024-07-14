package com.sillim.recordit.global.exception.feed;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidFeedImageCountException extends ApplicationException {
	public InvalidFeedImageCountException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidFeedImageCountException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
