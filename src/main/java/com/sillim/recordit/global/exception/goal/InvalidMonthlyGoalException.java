package com.sillim.recordit.global.exception.goal;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidMonthlyGoalException extends ApplicationException {

	public InvalidMonthlyGoalException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidMonthlyGoalException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
