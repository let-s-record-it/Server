package com.sillim.recordit.global.exception.security;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidIdTokenException extends ApplicationException {

	public InvalidIdTokenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
