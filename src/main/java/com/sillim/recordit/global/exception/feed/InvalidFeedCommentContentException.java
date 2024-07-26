package com.sillim.recordit.global.exception.feed;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidFeedCommentContentException extends ApplicationException {

	public InvalidFeedCommentContentException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidFeedCommentContentException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
