package com.sillim.recordit.global.exception.feed;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidFeedImageUrlException extends ApplicationException {
	public InvalidFeedImageUrlException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidFeedImageUrlException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
