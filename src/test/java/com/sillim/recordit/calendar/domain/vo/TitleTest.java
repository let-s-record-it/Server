package com.sillim.recordit.calendar.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TitleTest {

	@Test
	@DisplayName("공백이 아닌 30 이하의 문자열로 제목을 생성할 수 있다.")
	void validIfTitleIsNotBlankAnd30OrUnderLength() {
		Title title = new Title("title");

		assertAll(
				() -> {
					assertThat(title).isEqualTo(new Title("title"));
					assertThat(title.getTitle()).isEqualTo("title");
				});
	}

	@Test
	@DisplayName("문자열이 null이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleIsNull() {
		assertThatCode(() -> new Title(null))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.NULL_CALENDAR_TITLE.getDescription());
	}

	@Test
	@DisplayName("문자열이 공백이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleIsBlank() {
		assertThatCode(() -> new Title("    "))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.BLANK_CALENDAR_TITLE.getDescription());
	}

	@Test
	@DisplayName("문자열의 길이가 31 이상이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleLengthIs30Over() {
		assertThatCode(() -> new Title("1234567890123456789012345678901"))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.INVALID_CALENDAR_TITLE_LENGTH.getDescription());
	}
}
