package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class InvalidTitleException extends ApplicationException {

	public InvalidTitleException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidTitleException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
