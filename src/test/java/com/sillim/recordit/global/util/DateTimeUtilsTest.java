package com.sillim.recordit.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.sillim.recordit.global.exception.common.DayOfMonthOutOfRangeException;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateTimeUtilsTest {

	@Test
	@DisplayName("LocalDate를 목표 dayOfMonth에 맞게 보정하여 반환한다.")
	void correctDayOfMonthTest() {
		LocalDate corrected = DateTimeUtils.correctDayOfMonth(LocalDate.of(2024, 3, 12), 31);
		assertThat(corrected).hasDayOfMonth(31);
	}

	@Test
	@DisplayName("LocalDate의 월의 마지막 일이 목표 dayOfMonth 보다 작다면 마지막 일을 반환한다.")
	void returnLastDayOfMonthIfDateLessThanTargetDayOfMonth() {
		LocalDate corrected = DateTimeUtils.correctDayOfMonth(LocalDate.of(2024, 2, 12), 31);
		assertThat(corrected).hasDayOfMonth(29);
	}

	@Test
	@DisplayName("LocalDate의 월의 마지막 일이 목표 dayOfMonth 보다 크다면 dayOfMonth를 반환한다.")
	void returnTargetDayOfMonthIfDateGreaterThanTargetDayOfMonth() {
		LocalDate corrected = DateTimeUtils.correctDayOfMonth(LocalDate.of(2024, 3, 31), 28);
		assertThat(corrected).hasDayOfMonth(28);
	}

	@Test
	@DisplayName("dayOfMonth 값이 1보다 작다면 DayOfMonthOutOfRangeException이 발생한다.")
	void throwDayOfMonthOutOfRangeExceptionIfDayOfMonthIsLessThan1() {
		assertThatCode(() -> DateTimeUtils.correctDayOfMonth(LocalDate.of(2024, 3, 31), -1))
				.isInstanceOf(DayOfMonthOutOfRangeException.class).hasMessage("dayOfMonth는 1 이상 31 이하여야 합니다.");
	}

	@Test
	@DisplayName("dayOfMonth 값이 31보다 크다면 DayOfMonthOutOfRangeException이 발생한다.")
	void throwDayOfMonthOutOfRangeExceptionIfDayOfMonthIsGreaterThan1() {
		assertThatCode(() -> DateTimeUtils.correctDayOfMonth(LocalDate.of(2024, 3, 31), 32))
				.isInstanceOf(DayOfMonthOutOfRangeException.class).hasMessage("dayOfMonth는 1 이상 31 이하여야 합니다.");
	}
}
