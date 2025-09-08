package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidDayOfMonthException;
import com.sillim.recordit.schedule.domain.RepetitionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DayOfMonthTest {

	@Test
	@DisplayName("월간 반복을 위한 일자를 생성할 때 1 이상 31이하로 생성할 수 있다.")
	void validIfDateIs1OrOverAnd31OrUnderForMonthlyRepeating() {
		DayOfMonth dayOfMonth1 = DayOfMonth.createMonthly(1);
		DayOfMonth dayOfMonth2 = DayOfMonth.createMonthly(31);

		assertAll(() -> {
			assertThat(dayOfMonth1).isEqualTo(DayOfMonth.createMonthly(1));
			assertThat(dayOfMonth2).isEqualTo(DayOfMonth.createMonthly(31));
			assertThat(dayOfMonth1.equalsDayOfMonthValue(1)).isTrue();
			assertThat(dayOfMonth2.equalsDayOfMonthValue(31)).isTrue();
		});
	}

	@Test
	@DisplayName("연간 반복을 위한 일자를 생성할 때 월에 따른 날짜 사이로 생성할 수 있다. (2월은 윤년을 최대 일자로 한다.)")
	void validIfDateIsInMonthForYearlyRepeating() {
		assertAll(() -> {
			assertThatCode(() -> DayOfMonth.createYearly(2, 29)).doesNotThrowAnyException();
			assertThatCode(() -> DayOfMonth.createYearly(12, 31)).doesNotThrowAnyException();
		});
	}

	@Test
	@DisplayName("월간 반복을 위한 일자가 1 미만일 시 InvalidDayOfMonthException이 발생한다.")
	void throwInvalidDayOfMonthExceptionIfDayOfMonthIs1UnderForMonthlyRepeating() {
		assertThatCode(() -> DayOfMonth.createMonthly(0)).isInstanceOf(InvalidDayOfMonthException.class)
				.hasMessage(ErrorCode.INVALID_DAY_OF_MONTH.getDescription());
	}

	@Test
	@DisplayName("월간 반복을 위한 일자가 31 초과일 시 InvalidDayOfMonthException이 발생한다.")
	void throwInvalidDayOfMonthExceptionIfDayOfMonthIs12OverForMonthlyRepeating() {
		assertThatCode(() -> DayOfMonth.createMonthly(32)).isInstanceOf(InvalidDayOfMonthException.class)
				.hasMessage(ErrorCode.INVALID_DAY_OF_MONTH.getDescription());
	}

	@Test
	@DisplayName("연간 반복을 위한 일자가 해당 월 범위에 맞지 않을 시 InvalidDayOfMonthException이 발생한다.")
	void throwInvalidDayOfMonthExceptionIfDayOfMonthIsNotInMonthForYearlyRepeating() {
		assertAll(() -> {
			assertThatCode(() -> DayOfMonth.createYearly(1, 0)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.INVALID_DAY_OF_MONTH.getDescription());
			assertThatCode(() -> DayOfMonth.createYearly(2, 31)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.INVALID_DAY_OF_MONTH.getDescription());
			assertThatCode(() -> DayOfMonth.createYearly(4, 31)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.INVALID_DAY_OF_MONTH.getDescription());
			assertThatCode(() -> DayOfMonth.createYearly(5, 32)).isInstanceOf(InvalidDayOfMonthException.class)
					.hasMessage(ErrorCode.INVALID_DAY_OF_MONTH.getDescription());
		});
	}

	@Test
	@DisplayName("RepetitionType에 따른 DayOfMonth를 생성할 수 있다.")
	void createDayOfMonthForRepetitionType() {
		DayOfMonth dayOfMonth1 = DayOfMonth.create(RepetitionType.MONTHLY_WITH_DATE, null, 1);
		DayOfMonth dayOfMonth2 = DayOfMonth.create(RepetitionType.YEARLY_WITH_LAST_DAY, 1, 1);

		assertAll(() -> {
			assertThat(dayOfMonth1).isEqualTo(DayOfMonth.createMonthly(1));
			assertThat(dayOfMonth2).isEqualTo(DayOfMonth.createYearly(1, 1));
			assertThat(dayOfMonth1.equalsDayOfMonthValue(1)).isTrue();
			assertThat(dayOfMonth2.equalsDayOfMonthValue(1)).isTrue();
		});
	}
}
