package com.sillim.recordit.task.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskTitleTest {

	@Test
	@DisplayName("공백이 아닌 30 이하의 문자열로 제목을 생성할 수 있다.")
	void validIfTitleIsNotBlankAnd30OrUnderLength() {
		TaskTitle taskTitle = new TaskTitle("title");

		assertAll(() -> {
			assertThat(taskTitle).isEqualTo(new TaskTitle("title"));
			assertThat(taskTitle.getTitle()).isEqualTo("title");
		});
	}

	@Test
	@DisplayName("문자열이 null이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleIsNull() {
		assertThatCode(() -> new TaskTitle(null)).isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.NULL_TASK_TITLE.getDescription());
	}

	@Test
	@DisplayName("문자열이 공백이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleIsBlank() {
		assertThatCode(() -> new TaskTitle("    ")).isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.BLANK_TASK_TITLE.getDescription());
	}

	@Test
	@DisplayName("문자열의 길이가 31 이상이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleLengthIs30Over() {
		assertThatCode(() -> new TaskTitle("1234567890123456789012345678901")).isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.INVALID_TASK_TITLE_LENGTH.getDescription());
	}
}
