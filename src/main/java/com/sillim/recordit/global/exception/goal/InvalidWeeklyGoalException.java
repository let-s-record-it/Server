package com.sillim.recordit.global.exception.goal;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidWeeklyGoalException extends ApplicationException {

	public InvalidWeeklyGoalException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidWeeklyGoalException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
