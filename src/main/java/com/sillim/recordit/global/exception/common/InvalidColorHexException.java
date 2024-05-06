package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class InvalidColorHexException extends ApplicationException {

	public InvalidColorHexException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidColorHexException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
