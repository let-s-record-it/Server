package com.sillim.recordit.global.exception.calendar;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidCalendarException extends ApplicationException {

	public InvalidCalendarException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidCalendarException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
