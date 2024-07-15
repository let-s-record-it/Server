package com.sillim.recordit.global.exception.common;

import com.sillim.recordit.global.exception.ErrorCode;

public class FileGenerateException extends ApplicationException {

    public FileGenerateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileGenerateException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
