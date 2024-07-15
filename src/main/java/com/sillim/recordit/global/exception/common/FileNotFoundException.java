package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class FileNotFoundException extends ApplicationException {

	public FileNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}

	public FileNotFoundException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
