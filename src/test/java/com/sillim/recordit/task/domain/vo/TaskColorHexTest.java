package com.sillim.recordit.task.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidColorHexException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskColorHexTest {

	@Test
	@DisplayName("color hex 값에 맞는 ColorHex를 생성할 수 있다.")
	void validIfFitColorHex() {
		TaskColorHex taskColorHex1 = new TaskColorHex("112233");
		TaskColorHex taskColorHex2 = new TaskColorHex("ffda12fb");
		TaskColorHex taskColorHex3 = new TaskColorHex("fab");

		assertAll(
				() -> {
					assertThat(taskColorHex1).isEqualTo(new TaskColorHex("112233"));
					assertThat(taskColorHex2).isEqualTo(new TaskColorHex("ffda12fb"));
					assertThat(taskColorHex3).isEqualTo(new TaskColorHex("fab"));
					assertThat(taskColorHex1.getColorHex()).isEqualTo("112233");
					assertThat(taskColorHex2.getColorHex()).isEqualTo("ffda12fb");
					assertThat(taskColorHex3.getColorHex()).isEqualTo("fab");
				});
	}

	@Test
	@DisplayName("color hex 값이 null일 경우 InvalidColorHexException이 발생한다.")
	void throwInvalidColorHexExceptionIfNull() {
		assertThatCode(() -> new TaskColorHex(null))
				.isInstanceOf(InvalidColorHexException.class)
				.hasMessage(ErrorCode.NULL_TASK_COLOR_HEX.getDescription());
	}

	@Test
	@DisplayName("color hex 값에 맞지 않을 시 InvalidColorHexException이 발생한다.")
	void throwInvalidColorHexExceptionIfNotFitColorHex() {
		assertAll(
				() -> {
					assertThatCode(() -> new TaskColorHex("11233"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_TASK_COLOR_HEX.getDescription());
					assertThatCode(() -> new TaskColorHex("ggg"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_TASK_COLOR_HEX.getDescription());
					assertThatCode(() -> new TaskColorHex("11233"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_TASK_COLOR_HEX.getDescription());
					assertThatCode(() -> new TaskColorHex("ffda12f"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_TASK_COLOR_HEX.getDescription());
					assertThatCode(() -> new TaskColorHex("fb"))
							.isInstanceOf(InvalidColorHexException.class)
							.hasMessage(ErrorCode.INVALID_TASK_COLOR_HEX.getDescription());
				});
	}
}
