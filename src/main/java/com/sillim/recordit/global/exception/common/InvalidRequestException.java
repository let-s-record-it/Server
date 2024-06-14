package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class InvalidRequestException extends ApplicationException {

	public InvalidRequestException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidRequestException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
