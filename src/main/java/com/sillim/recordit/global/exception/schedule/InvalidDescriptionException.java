package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidDescriptionException extends ApplicationException {

	public InvalidDescriptionException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidDescriptionException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
