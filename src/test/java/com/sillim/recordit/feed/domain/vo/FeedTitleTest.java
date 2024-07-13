package com.sillim.recordit.feed.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedTitleTest {

	@Test
	@DisplayName("공백이 아닌 30 이하의 문자열로 FeedTitle을 생성할 수 있다.")
	void validIfTitleIsNotBlankAnd30OrUnderLength() {
		FeedTitle feedTitle = new FeedTitle("title");

		assertAll(
				() -> {
					assertThat(feedTitle).isEqualTo(new FeedTitle("title"));
					assertThat(feedTitle.getTitle()).isEqualTo("title");
				});
	}

	@Test
	@DisplayName("FeedTitle이 null이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleIsNull() {
		assertThatCode(() -> new FeedTitle(null))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.NULL_FEED_TITLE.getDescription());
	}

	@Test
	@DisplayName("FeedTitle이 공백이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleIsBlank() {
		assertThatCode(() -> new FeedTitle("    "))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.BLANK_FEED_TITLE.getDescription());
	}

	@Test
	@DisplayName("문자열의 길이가 31 이상이면 InvalidTitleException 예외가 발생한다.")
	void throwInvalidTitleExceptionIfTitleLengthIs30Over() {
		assertThatCode(() -> new FeedTitle("1234567890123456789012345678901"))
				.isInstanceOf(InvalidTitleException.class)
				.hasMessage(ErrorCode.INVALID_FEED_TITLE_LENGTH.getDescription());
	}
}
