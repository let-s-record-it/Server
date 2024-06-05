package com.sillim.recordit.goal.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WeeklyGoalPeriodTest {

	@Test
	@DisplayName("WeeklyGoalPeriod를 생성한다.")
	void newWeeklyGoalPeriodTest() {
		assertThatCode(
						() ->
								new WeeklyGoalPeriod(
										LocalDate.of(2024, 4, 28), LocalDate.of(2024, 5, 4)))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("startDate와 endDate는 null이 아니어야 한다.")
	void validateNullTest() {

		assertThatThrownBy(() -> new WeeklyGoalPeriod(null, null))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());

		assertThatThrownBy(() -> new WeeklyGoalPeriod(null, LocalDate.of(2024, 5, 4)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());

		assertThatThrownBy(() -> new WeeklyGoalPeriod(LocalDate.of(2024, 4, 28), null))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());
	}

	@Test
	@DisplayName("주 목표의 시작일은 일요일이어야 한다.")
	void validateIsSundayTest() {

		assertThatThrownBy(
						() ->
								new WeeklyGoalPeriod(
										LocalDate.of(2024, 4, 27), LocalDate.of(2025, 5, 3)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.INVALID_START_DAY_OF_WEEK.getDescription());
	}

	@Test
	@DisplayName("주 목표의 시작일과 종료일은 6일 차이가 나야 한다.")
	void validateDifferenceOfDateTest() {

		assertThatThrownBy(
						() ->
								new WeeklyGoalPeriod(
										LocalDate.of(2024, 4, 28), LocalDate.of(2024, 5, 3)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.INVALID_DIFFERENCE_OF_DATE.getDescription());
	}
}
