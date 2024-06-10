package com.sillim.recordit.global.advice.task;

import com.sillim.recordit.global.domain.MethodSignature;
import com.sillim.recordit.global.dto.response.ErrorResponse;
import com.sillim.recordit.global.exception.common.ApplicationException;
import com.sillim.recordit.global.exception.common.InvalidColorHexException;
import com.sillim.recordit.global.exception.common.InvalidDescriptionException;
import com.sillim.recordit.global.exception.common.InvalidTitleException;
import com.sillim.recordit.global.exception.schedule.InvalidDayOfMonthException;
import com.sillim.recordit.global.exception.schedule.InvalidMonthOfYearException;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.global.exception.schedule.InvalidWeekdayBitException;
import com.sillim.recordit.global.util.LoggingUtils;
import com.sillim.recordit.task.controller.TaskController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

@RestControllerAdvice(basePackageClasses = TaskController.class)
public class TaskControllerAdvice {

	@ExceptionHandler({
		InvalidTitleException.class,
		InvalidDescriptionException.class,
		InvalidColorHexException.class,
		InvalidMonthOfYearException.class,
		InvalidDayOfMonthException.class,
		InvalidWeekdayBitException.class,
		InvalidRepetitionException.class,
	})
	public ResponseEntity<ErrorResponse> handleBadRequest(
			HandlerMethod handlerMethod, ApplicationException exception) {

		LoggingUtils.exceptionLog(
				MethodSignature.extract(handlerMethod), HttpStatus.BAD_REQUEST, exception);

		return ResponseEntity.badRequest().body(ErrorResponse.from(exception.getErrorCode()));
	}
}
