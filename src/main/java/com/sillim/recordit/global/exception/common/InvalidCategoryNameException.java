package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class InvalidCategoryNameException extends ApplicationException {

	public InvalidCategoryNameException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidCategoryNameException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
