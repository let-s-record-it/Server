package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidScheduleDurationException extends ApplicationException {

	public InvalidScheduleDurationException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidScheduleDurationException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
