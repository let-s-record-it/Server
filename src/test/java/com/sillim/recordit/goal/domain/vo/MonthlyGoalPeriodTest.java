package com.sillim.recordit.goal.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MonthlyGoalPeriodTest {

	@Test
	@DisplayName("MonthlyGoalPeriod를 생성한다.")
	void newMonthlyGoalPeriodTest() {

		assertThatCode(
						() ->
								new MonthlyGoalPeriod(
										LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31)))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("startDate와 endDate는 null이 아니어야 한다.")
	void validateNullTest() {

		assertThatThrownBy(() -> new MonthlyGoalPeriod(null, null))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());

		assertThatThrownBy(() -> new MonthlyGoalPeriod(null, LocalDate.of(2024, 5, 31)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());

		assertThatThrownBy(() -> new MonthlyGoalPeriod(LocalDate.of(2024, 5, 1), null))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());
	}

	@Test
	@DisplayName("startDate와 endDate의 년, 월이 서로 같지 않다면 InvalidPeriodException이 발생한다.")
	void validateDifferentYearMonthTest() {

		assertThatThrownBy(
						() ->
								new MonthlyGoalPeriod(
										LocalDate.of(2024, 5, 1), LocalDate.of(2025, 6, 30)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.DIFFERENT_YEAR_MONTH.getDescription());

		assertThatThrownBy(
						() ->
								new MonthlyGoalPeriod(
										LocalDate.of(2024, 5, 1), LocalDate.of(2024, 6, 30)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.DIFFERENT_YEAR_MONTH.getDescription());

		assertThatThrownBy(
						() ->
								new MonthlyGoalPeriod(
										LocalDate.of(2024, 5, 1), LocalDate.of(2025, 5, 31)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.DIFFERENT_YEAR_MONTH.getDescription());
	}

	@Test
	@DisplayName("startDate가 1이 아니라면 InvalidPeriodException이 발생한다.")
	void validateInvalidStartDayOfMonthTest() {

		assertThatThrownBy(
						() ->
								new MonthlyGoalPeriod(
										LocalDate.of(2024, 5, 2), LocalDate.of(2024, 5, 31)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.INVALID_START_DAY_OF_MONTH.getDescription());
	}

	@Test
	@DisplayName("endDate가 해당 월의 말일이 아니라면 InvalidPeriodException이 발생한다.")
	void validateInvalidEndDayOfMonthTest() {

		assertThatThrownBy(
						() ->
								new MonthlyGoalPeriod(
										LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 28)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.INVALID_END_DAY_OF_MONTH.getFormattedDescription(31));
	}
}
