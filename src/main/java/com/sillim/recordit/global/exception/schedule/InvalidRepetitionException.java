package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidRepetitionException extends ApplicationException {

	public InvalidRepetitionException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidRepetitionException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
