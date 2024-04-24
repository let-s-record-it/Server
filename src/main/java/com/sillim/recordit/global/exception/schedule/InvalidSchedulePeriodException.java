package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidSchedulePeriodException extends ApplicationException {

	public InvalidSchedulePeriodException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidSchedulePeriodException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
