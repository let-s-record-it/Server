package com.sillim.recordit.goal.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidPeriodException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WeeklyGoalPeriodTest {

	@Test
	@DisplayName("시작일이 일요일인 WeeklyGoalPeriod를 생성한다.")
	void newWeeklyGoalPeriod_StartDateIsSundayTest() {
		assertThatCode(() -> new WeeklyGoalPeriod(5, LocalDate.of(2024, 4, 28), LocalDate.of(2024, 5, 4)))
				.doesNotThrowAnyException();
		assertThatCode(() -> new WeeklyGoalPeriod(3, LocalDate.of(2024, 8, 11), LocalDate.of(2024, 8, 17)))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("week, startDate, endDate가 null이라면 InvalidPeriodException이 발생한다.")
	void validateNullTest() {

		assertAll(() -> {
			assertThatThrownBy(() -> new WeeklyGoalPeriod(null, null, null)).isInstanceOf(InvalidPeriodException.class)
					.hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());

			assertThatThrownBy(() -> new WeeklyGoalPeriod(null, null, LocalDate.of(2024, 5, 4)))
					.isInstanceOf(InvalidPeriodException.class).hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());

			assertThatThrownBy(() -> new WeeklyGoalPeriod(null, LocalDate.of(2024, 4, 28), null))
					.isInstanceOf(InvalidPeriodException.class).hasMessage(ErrorCode.NULL_GOAL_PERIOD.getDescription());
		});
	}

	@Test
	@DisplayName("주 목표의 시작일이 일요일이 아니라면 InvalidPeriodException이 발생한다.")
	void validateStartDateIsSundayOrMondayTest() {

		assertAll(() -> {
			assertThatThrownBy(() -> new WeeklyGoalPeriod(5, LocalDate.of(2024, 4, 27), LocalDate.of(2025, 5, 3)))
					.isInstanceOf(InvalidPeriodException.class)
					.hasMessage(ErrorCode.INVALID_START_DAY_OF_WEEK.getDescription());
			assertThatThrownBy(() -> new WeeklyGoalPeriod(5, LocalDate.of(2024, 4, 30), LocalDate.of(2025, 5, 6)))
					.isInstanceOf(InvalidPeriodException.class)
					.hasMessage(ErrorCode.INVALID_START_DAY_OF_WEEK.getDescription());
		});
	}

	@Test
	@DisplayName("주 목표의 시작일과 종료일이 6일 차이 나지 않는다면 InvalidPeriodException이 발생한다.")
	void validateDifferenceOfDateTest() {

		assertThatThrownBy(() -> new WeeklyGoalPeriod(5, LocalDate.of(2024, 4, 28), LocalDate.of(2024, 5, 3)))
				.isInstanceOf(InvalidPeriodException.class)
				.hasMessage(ErrorCode.INVALID_DIFFERENCE_OF_DATE.getDescription());
	}
}
