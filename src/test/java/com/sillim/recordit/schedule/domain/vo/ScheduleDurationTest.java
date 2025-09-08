package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidScheduleDurationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleDurationTest {

	@Test
	@DisplayName("시작 시간이 끝 시간보다 작은 ScheduleDuration을 생성할 수 있다.")
	void validScheduleDurationIfStartDateIsBeforeEndDate() {
		LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2024, 1, 2, 0, 0);
		ScheduleDuration scheduleDuration = ScheduleDuration.createNotAllDay(start, end);
		ScheduleDuration allDayScheduleDuration = ScheduleDuration.createAllDay(start, end);

		assertAll(() -> {
			assertThat(scheduleDuration).isEqualTo(ScheduleDuration.createNotAllDay(start, end));
			assertThat(allDayScheduleDuration).isEqualTo(ScheduleDuration.createAllDay(start, end));
			assertThat(allDayScheduleDuration).isNotEqualTo(ScheduleDuration.createNotAllDay(start, end));
		});
	}

	@Test
	@DisplayName("시작시간이 끝 시간보다 크면 InvalidScheduleDurationException이 발생한다.")
	void throwInvalidScheduleDurationExceptionIfStartDateIsAfterEndDate() {
		LocalDateTime start = LocalDateTime.of(2024, 1, 2, 0, 0);
		LocalDateTime end = LocalDateTime.of(2024, 1, 1, 0, 0);
		assertAll(() -> {
			assertThatCode(() -> ScheduleDuration.createNotAllDay(start, end))
					.isInstanceOf(InvalidScheduleDurationException.class)
					.hasMessage(ErrorCode.INVALID_DURATION.getDescription());
			assertThatCode(() -> ScheduleDuration.createAllDay(start, end))
					.isInstanceOf(InvalidScheduleDurationException.class)
					.hasMessage(ErrorCode.INVALID_DURATION.getDescription());
		});
	}

	@Test
	@DisplayName("종일인 경우 시간은 비교에 영향을 주지 않는다.")
	void validScheduleDurationIfIsAllDayEvenIfStartTimeIsAfterEndTime() {
		LocalDateTime start = LocalDateTime.of(2024, 1, 1, 11, 0);
		LocalDateTime end = LocalDateTime.of(2024, 1, 1, 10, 0);
		assertAll(() -> {
			assertThatCode(() -> ScheduleDuration.createAllDay(start, end)).doesNotThrowAnyException();
		});
	}
}
