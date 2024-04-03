package com.sillim.recordit.goal.exception;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class MonthlyGoalQueryException extends ApplicationException {

	public MonthlyGoalQueryException(ErrorCode errorCode) {
		super(errorCode);
	}
}
