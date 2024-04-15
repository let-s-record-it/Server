package com.sillim.recordit.global.exception.member;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidAccessTokenException extends ApplicationException {

	public InvalidAccessTokenException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidAccessTokenException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
