package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidMonthOfYearException extends ApplicationException {

	public InvalidMonthOfYearException(ErrorCode errorCode) {
		super(errorCode);
	}

	public InvalidMonthOfYearException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
