package com.sillim.recordit.global.exception.member;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidRejoinException extends ApplicationException {
	public InvalidRejoinException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidRejoinException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
