package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class RecordNotFoundException extends ApplicationException {

	public RecordNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}

	public RecordNotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
