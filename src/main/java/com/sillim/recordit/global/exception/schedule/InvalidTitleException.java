package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidTitleException extends ApplicationException {

	public InvalidTitleException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidTitleException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
