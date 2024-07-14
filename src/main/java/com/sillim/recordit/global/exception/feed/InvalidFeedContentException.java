package com.sillim.recordit.global.exception.feed;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidFeedContentException extends ApplicationException {
	public InvalidFeedContentException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidFeedContentException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
