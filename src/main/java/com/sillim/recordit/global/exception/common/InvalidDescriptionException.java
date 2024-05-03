package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class InvalidDescriptionException extends ApplicationException {

	public InvalidDescriptionException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidDescriptionException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
