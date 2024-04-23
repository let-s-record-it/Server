package com.sillim.recordit.global.exception.security;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidJwtException extends ApplicationException {

	public InvalidJwtException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidJwtException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
