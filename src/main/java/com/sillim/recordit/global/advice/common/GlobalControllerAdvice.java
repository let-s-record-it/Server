package com.sillim.recordit.global.advice.common;

import com.sillim.recordit.global.domain.MethodSignature;
import com.sillim.recordit.global.dto.response.ErrorResponse;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.util.LoggingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> noHandlerFound(NoHandlerFoundException exception) {

		LoggingUtils.exceptionLog(HttpStatus.BAD_REQUEST, exception);
		return ResponseEntity.badRequest().body(ErrorResponse.from(ErrorCode.REQUEST_NOT_FOUND));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ResponseEntity<Void> handleHttpMessageNotReadableException(
			HandlerMethod handlerMethod, HttpMessageNotReadableException exception) {

		LoggingUtils.exceptionLog(
				MethodSignature.extract(handlerMethod), HttpStatus.METHOD_NOT_ALLOWED, exception);

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> methodArgumentNotValid(
			HandlerMethod handlerMethod, MethodArgumentNotValidException exception) {

		String message = LoggingUtils.methodArgumentNotValidMessage(exception);
		LoggingUtils.exceptionLog(
				MethodSignature.extract(handlerMethod), HttpStatus.BAD_REQUEST, exception, message);

		return ResponseEntity.badRequest()
				.body(ErrorResponse.from(ErrorCode.INVALID_ARGUMENT, message));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			HandlerMethod handlerMethod, MethodArgumentTypeMismatchException exception) {

		String message = LoggingUtils.methodArgumentTypeMismatchMessage(exception);
		LoggingUtils.exceptionLog(
				MethodSignature.extract(handlerMethod), HttpStatus.BAD_REQUEST, exception, message);

		return ResponseEntity.badRequest()
				.body(ErrorResponse.from(ErrorCode.INVALID_ARGUMENT, message));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleMissingRequestParameterException(
			HandlerMethod handlerMethod, MissingServletRequestParameterException exception) {

		String message = LoggingUtils.missingRequestParameterMessage(exception);
		LoggingUtils.exceptionLog(
				MethodSignature.extract(handlerMethod), HttpStatus.BAD_REQUEST, exception, message);

		ErrorResponse errorResponse = ErrorResponse.from(ErrorCode.INVALID_ARGUMENT, message);
		return ResponseEntity.badRequest().body(errorResponse);
	}
}
