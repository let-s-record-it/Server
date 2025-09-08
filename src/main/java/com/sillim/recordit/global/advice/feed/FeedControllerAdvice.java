package com.sillim.recordit.global.advice.feed;

import com.sillim.recordit.global.dto.response.ErrorResponse;
import com.sillim.recordit.global.exception.common.ApplicationException;
import com.sillim.recordit.global.exception.feed.InvalidFeedContentException;
import com.sillim.recordit.global.exception.feed.InvalidFeedImageUrlException;
import com.sillim.recordit.global.util.LoggingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FeedControllerAdvice {

	@ExceptionHandler({InvalidFeedContentException.class, InvalidFeedImageUrlException.class,})
	public ResponseEntity<ErrorResponse> handleInvalidFeedRequest(ApplicationException exception) {
		LoggingUtils.exceptionLog(HttpStatus.BAD_REQUEST, exception);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorResponse.from(exception.getErrorCode(), exception.getMessage()));
	}
}
