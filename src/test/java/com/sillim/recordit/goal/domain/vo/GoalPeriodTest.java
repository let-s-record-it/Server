package com.sillim.recordit.goal.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GoalPeriodTest {

	@Test
	@DisplayName("GoalPeriod를 생성한다.")
	void newGoalColorHexTest() {

		assertThatCode(() -> new GoalPeriod(LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31)))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("startDate와 endDate는 null이 아니어야 한다.")
	void validateNullTest() {

		assertThatThrownBy(() -> new GoalPeriod(null, null))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());

		assertThatThrownBy(() -> new GoalPeriod(null, LocalDate.of(2024, 5, 31)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());

		assertThatThrownBy(() -> new GoalPeriod(LocalDate.of(2024, 5, 1), null))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());
	}

	@Test
	@DisplayName("startDate와 endDate의 년, 월은 서로 같아야 한다.")
	void validateDifferentYearMonthTest() {

		assertThatThrownBy(
						() -> new GoalPeriod(LocalDate.of(2024, 5, 1), LocalDate.of(2025, 6, 30)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.DIFFERENT_YEAR_MONTH.getDescription());

		assertThatThrownBy(
						() -> new GoalPeriod(LocalDate.of(2024, 5, 1), LocalDate.of(2024, 6, 30)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.DIFFERENT_YEAR_MONTH.getDescription());

		assertThatThrownBy(
						() -> new GoalPeriod(LocalDate.of(2024, 5, 1), LocalDate.of(2025, 5, 31)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.DIFFERENT_YEAR_MONTH.getDescription());
	}

	@Test
	@DisplayName("startDate는 1이어야 한다.")
	void validateInvalidStartDayOfMonthTest() {

		assertThatThrownBy(
						() -> new GoalPeriod(LocalDate.of(2024, 5, 2), LocalDate.of(2024, 5, 31)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.INVALID_START_DAY_OF_MONTH.getDescription());
	}

	@Test
	@DisplayName("endDate는 해당 월의 말일이어야 한다.")
	void validateInvalidEndDayOfMonthTest() {

		assertThatThrownBy(
						() -> new GoalPeriod(LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 28)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.INVALID_END_DAY_OF_MONTH.getFormattedDescription(31));
	}
}