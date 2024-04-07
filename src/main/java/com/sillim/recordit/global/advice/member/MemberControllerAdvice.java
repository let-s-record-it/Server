package com.sillim.recordit.global.advice.member;

import com.sillim.recordit.global.dto.response.ErrorResponse;
import com.sillim.recordit.global.exception.member.InvalidAccessTokenException;
import com.sillim.recordit.global.exception.member.InvalidIdTokenException;
import com.sillim.recordit.global.util.LoggingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MemberControllerAdvice {

	@ExceptionHandler(InvalidIdTokenException.class)
	public ResponseEntity<ErrorResponse> invalidIdToken(InvalidIdTokenException exception) {
		LoggingUtils.exceptionLog(HttpStatus.UNAUTHORIZED, exception);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ErrorResponse.from(exception.getErrorCode(), exception.getMessage()));
	}

	@ExceptionHandler(InvalidAccessTokenException.class)
	public ResponseEntity<ErrorResponse> invalidAccessToken(InvalidAccessTokenException exception) {
		LoggingUtils.exceptionLog(HttpStatus.UNAUTHORIZED, exception);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ErrorResponse.from(exception.getErrorCode(), exception.getMessage()));
	}
}
