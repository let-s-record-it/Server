package com.sillim.recordit.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.enums.date.WeekNumber;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WeekNumberTest {

	@Test
	@DisplayName("날짜가 WeekNumber에 해당한다면 true를 반환한다.")
	void returnTrueIfWeekNumberContainsDate() {

		assertAll(() -> {
			assertThat(WeekNumber.FIRST.contains(LocalDate.of(2024, 6, 1))).isTrue();
			assertThat(WeekNumber.SECOND.contains(LocalDate.of(2024, 6, 8))).isTrue();
			assertThat(WeekNumber.THIRD.contains(LocalDate.of(2024, 6, 15))).isTrue();
			assertThat(WeekNumber.FOURTH.contains(LocalDate.of(2024, 6, 22))).isTrue();
			assertThat(WeekNumber.FIFTH.contains(LocalDate.of(2024, 6, 29))).isTrue();
		});
	}

	@Test
	@DisplayName("날짜가 WeekNumber에 해당하지 않는다면 false를 반환한다.")
	void returnFalseIfWeekNumberNotContainsDate() {

		assertAll(() -> {
			assertThat(WeekNumber.FIRST.contains(LocalDate.of(2024, 6, 8))).isFalse();
			assertThat(WeekNumber.SECOND.contains(LocalDate.of(2024, 6, 7))).isFalse();
			assertThat(WeekNumber.THIRD.contains(LocalDate.of(2024, 6, 14))).isFalse();
			assertThat(WeekNumber.FOURTH.contains(LocalDate.of(2024, 6, 29))).isFalse();
			assertThat(WeekNumber.FIFTH.contains(LocalDate.of(2024, 6, 1))).isFalse();
		});
	}
}
