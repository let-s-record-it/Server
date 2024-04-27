package com.sillim.recordit.global.exception.schedule;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.ApplicationException;

public class InvalidWeekdayBitException extends ApplicationException {

    public InvalidWeekdayBitException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidWeekdayBitException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
