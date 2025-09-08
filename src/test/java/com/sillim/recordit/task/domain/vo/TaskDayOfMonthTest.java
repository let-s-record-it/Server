package com.sillim.recordit.task.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidDayOfMonthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskDayOfMonthTest {

	@Test
	@DisplayName("월간 반복을 위한 일자를 생성할 때 1 이상 31이하로 생성할 수 있다.")
	void validIfDateIs1OrOverAnd31OrUnderForMonthlyRepeating() {
		TaskDayOfMonth dayOfMonth1 = TaskDayOfMonth.createMonthly(1);
		TaskDayOfMonth dayOfMonth2 = TaskDayOfMonth.createMonthly(31);

		assertAll(() -> {
			assertThat(dayOfMonth1).isEqualTo(TaskDayOfMonth.createMonthly(1));
			assertThat(dayOfMonth2).isEqualTo(TaskDayOfMonth.createMonthly(31));
		});
	}

	@Test
	@DisplayName("연간 반복을 위한 일자를 생성할 때 월에 따른 날짜 사이로 생성할 수 있다. (2월은 윤년을 최대 일자로 한다.)")
	void validIfDateIsInMonthForYearlyRepeating() {
		assertAll(() -> {
			assertThatCode(() -> TaskDayOfMonth.createYearly(2, 29)).doesNotThrowAnyException();
			assertThatCode(() -> TaskDayOfMonth.createYearly(12, 31)).doesNotThrowAnyException();
		});
	}

	@Test
	@DisplayName("월간 반복을 위한 일자가 null이라면 InvalidDayOfMonthException이 발생한다.")
	void throwInvalidDayOfMonthExceptionIfDayOfMonthIsNull() {
		assertThatCode(() -> TaskDayOfMonth.createMonthly(null)).isInstanceOf(InvalidDayOfMonthException.class)
				.hasMessage(ErrorCode.NULL_TASK_DAY_OF_MONTH.getDescription());
	}

	@Test
	@DisplayName("월간 반복을 위한 일자가 null이라면 InvalidDayOfMonthException이 발생한다.")
	void throwInvalidDayOfMonthExceptionIfDayOfMonthOrMonthOfYearIsNull() {
		assertAll(() -> {
			assertThatCode(() -> TaskDayOfMonth.createYearly(null, 29)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.NULL_TASK_MONTH_OF_YEAR.getDescription());
			assertThatCode(() -> TaskDayOfMonth.createYearly(2, null)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.NULL_TASK_DAY_OF_MONTH.getDescription());
		});
	}

	@Test
	@DisplayName("월간 반복을 위한 일자가 1 미만, 31 초과일 시 InvalidDayOfMonthException이 발생한다.")
	void throwInvalidDayOfMonthExceptionIfDayOfMonthIsOutOfRangeForMonthlyRepeating() {
		assertAll(() -> {
			// 반복 일자가 1 미만
			assertThatCode(() -> TaskDayOfMonth.createMonthly(0)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.TASK_DAY_OF_MONTH_OUT_OF_RANGE.getDescription());
			// 반복 일자가 31 초과
			assertThatCode(() -> TaskDayOfMonth.createMonthly(32)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.TASK_DAY_OF_MONTH_OUT_OF_RANGE.getDescription());
		});
	}

	@Test
	@DisplayName("연간 반복을 위한 일자가 해당 월 범위에 맞지 않을 시 InvalidDayOfMonthException이 발생한다.")
	void throwInvalidDayOfMonthExceptionIfDayOfMonthIsNotInMonthForYearlyRepeating() {
		assertAll(() -> {
			assertThatCode(() -> TaskDayOfMonth.createYearly(1, 0)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.TASK_DAY_OF_MONTH_OUT_OF_RANGE.getDescription());
			assertThatCode(() -> TaskDayOfMonth.createYearly(2, 31)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.TASK_DAY_OF_MONTH_OUT_OF_RANGE.getDescription());
			assertThatCode(() -> TaskDayOfMonth.createYearly(4, 31)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.TASK_DAY_OF_MONTH_OUT_OF_RANGE.getDescription());
			assertThatCode(() -> TaskDayOfMonth.createYearly(5, 32)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.TASK_DAY_OF_MONTH_OUT_OF_RANGE.getDescription());
		});
	}
}
