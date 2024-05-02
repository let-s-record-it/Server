package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidLocationException extends ApplicationException {

	public InvalidLocationException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidLocationException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
