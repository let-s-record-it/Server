package com.sillim.recordit.global.dto.response;

import com.sillim.recordit.global.exception.ErrorCode;

public record ErrorResponse(String errorCode, String message) {

	public static ErrorResponse from(ErrorCode errorCode) {

		return new ErrorResponse(errorCode.getCode(), errorCode.getDescription());
	}

	public static ErrorResponse from(ErrorCode errorCode, String description) {

		return new ErrorResponse(errorCode.getCode(), description);
	}
}
