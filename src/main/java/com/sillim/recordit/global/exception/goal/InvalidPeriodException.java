package com.sillim.recordit.global.exception.goal;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidPeriodException extends ApplicationException {

	public InvalidPeriodException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidPeriodException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
