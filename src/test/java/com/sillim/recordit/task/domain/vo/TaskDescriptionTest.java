package com.sillim.recordit.task.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidDescriptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskDescriptionTest {

	@Test
	@DisplayName("500자 이하인 설명을 생성할 수 있다.")
	void validIfDescriptionLengthIs500OrUnder() {
		TaskDescription taskDescription = new TaskDescription("123");

		assertAll(
				() -> {
					assertThat(taskDescription).isEqualTo(new TaskDescription("123"));
					assertThat(taskDescription.getDescription()).isEqualTo("123");
				});
	}

	@Test
	@DisplayName("설명이 null이면 InvalidDescriptionException이 발생한다.")
	void throwInvalidDescriptionExceptionIfDescriptionIsNull() {
		assertThatCode(() -> new TaskDescription(null))
				.isInstanceOf(InvalidDescriptionException.class)
				.hasMessage(ErrorCode.NULL_TASK_DESCRIPTION.getDescription());
	}

	@Test
	@DisplayName("설명이 500자 초과이면 InvalidDescriptionException이 발생한다.")
	void throwInvalidDescriptionExceptionIfDescriptionLengthIs500Over() {
		assertThatCode(() -> new TaskDescription("1234567890".repeat(51)))
				.isInstanceOf(InvalidDescriptionException.class)
				.hasMessage(ErrorCode.INVALID_TASK_DESCRIPTION_LENGTH.getDescription());
	}
}
