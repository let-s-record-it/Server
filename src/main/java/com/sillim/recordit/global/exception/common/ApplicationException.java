package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

	private final ErrorCode errorCode;

	public ApplicationException(ErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}

	public ApplicationException(ErrorCode errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
	}
}
