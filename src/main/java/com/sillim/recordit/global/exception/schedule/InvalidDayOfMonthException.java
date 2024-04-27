package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidDayOfMonthException extends ApplicationException {

	public InvalidDayOfMonthException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidDayOfMonthException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
