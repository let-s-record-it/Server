package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidColorHexException extends ApplicationException {

	public InvalidColorHexException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidColorHexException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
