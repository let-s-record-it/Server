package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class DayOfMonthOutOfRangeException extends ApplicationException {

	private static final String ERROR_MESSAGE = "dayOfMonth는 1 이상 31 이하여야 합니다.";

	public DayOfMonthOutOfRangeException() {
		super(ErrorCode.INVALID_ARGUMENT, ERROR_MESSAGE);
	}
}
