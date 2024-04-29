package com.sillim.recordit.schedule.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidDescriptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleDescriptionTest {

	@Test
	@DisplayName("500자 이하인 설명을 생성할 수 있다.")
	void validIfDescriptionLengthIs500OrUnder() {
		ScheduleDescription scheduleDescription = new ScheduleDescription("123");

		assertAll(
				() -> {
					assertThat(scheduleDescription).isEqualTo(new ScheduleDescription("123"));
					assertThat(scheduleDescription.getDescription()).isEqualTo("123");
				});
	}

	@Test
	@DisplayName("설명이 null이면 InvalidDescriptionException이 발생한다.")
	void throwInvalidDescriptionExceptionIfDescriptionIsNull() {
		assertThatCode(() -> new ScheduleDescription(null))
				.isInstanceOf(InvalidDescriptionException.class)
				.hasMessage(ErrorCode.NULL_SCHEDULE_DESCRIPTION.getDescription());
	}

	@Test
	@DisplayName("설명이 500자 초과이면 InvalidDescriptionException이 발생한다.")
	void throwInvalidDescriptionExceptionIfDescriptionLengthIs500Over() {
		assertThatCode(() -> new ScheduleDescription("1234567890".repeat(51)))
				.isInstanceOf(InvalidDescriptionException.class)
				.hasMessage(ErrorCode.INVALID_SCHEDULE_DESCRIPTION_LENGTH.getDescription());
	}
}
