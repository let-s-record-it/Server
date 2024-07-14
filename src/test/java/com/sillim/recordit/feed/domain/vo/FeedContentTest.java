package com.sillim.recordit.feed.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedContentTest {

	@Test
	@DisplayName("5000자 이하인 내용을 생성할 수 있다.")
	void validIfFeedContentLengthIs5000OrUnder() {
		FeedContent feedContent = new FeedContent("123");

		assertAll(
				() -> {
					assertThat(feedContent).isEqualTo(new FeedContent("123"));
					assertThat(feedContent.getContent()).isEqualTo("123");
				});
	}

	@Test
	@DisplayName("내용이 null이면 InvalidFeedContentException이 발생한다.")
	void throwInvalidFeedContentExceptionIfFeedContentIsNull() {
		assertThatCode(() -> new FeedContent(null))
				.isInstanceOf(InvalidFeedContentException.class)
				.hasMessage(ErrorCode.NULL_FEED_CONTENT.getDescription());
	}

	@Test
	@DisplayName("설명이 5000자 초과이면 InvalidFeedContentException이 발생한다.")
	void throwInvalidFeedContentExceptionIfFeedContentLengthIs5000Over() {
		assertThatCode(() -> new FeedContent("1234567890".repeat(501)))
				.isInstanceOf(InvalidFeedContentException.class)
				.hasMessage(ErrorCode.INVALID_FEED_CONTENT_LENGTH.getDescription());
	}
}
