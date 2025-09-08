package com.sillim.recordit.global.advice.goal;

import com.sillim.recordit.global.dto.response.ErrorResponse;
import com.sillim.recordit.global.exception.common.ApplicationException;
import com.sillim.recordit.global.exception.common.InvalidColorHexException;
import com.sillim.recordit.global.exception.common.InvalidDescriptionException;
import com.sillim.recordit.global.exception.common.InvalidTitleException;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import com.sillim.recordit.goal.controller.MonthlyGoalController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {MonthlyGoalController.class})
public class GoalControllerAdvice {

	@ExceptionHandler({InvalidTitleException.class, InvalidDescriptionException.class, InvalidPeriodException.class,
			InvalidColorHexException.class,})
	public ResponseEntity<ErrorResponse> handleBadRequest(ApplicationException exception) {

		return ResponseEntity.badRequest().body(ErrorResponse.from(exception.getErrorCode()));
	}
}
