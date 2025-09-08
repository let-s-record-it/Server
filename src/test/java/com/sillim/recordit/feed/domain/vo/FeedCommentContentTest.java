package com.sillim.recordit.feed.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedCommentContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedCommentContentTest {

	@Test
	@DisplayName("1000자 이하인 내용을 생성할 수 있다.")
	void validIfFeedCommentContentLengthIs1000OrUnder() {
		FeedCommentContent feedCommentContent = new FeedCommentContent("123");

		assertAll(() -> {
			assertThat(feedCommentContent).isEqualTo(new FeedCommentContent("123"));
			assertThat(feedCommentContent.getContent()).isEqualTo("123");
		});
	}

	@Test
	@DisplayName("내용이 null이면 InvalidFeedCommentContentException이 발생한다.")
	void throwInvalidFeedCommentContentExceptionIfFeedCommentContentIsNull() {
		assertThatCode(() -> new FeedCommentContent(null)).isInstanceOf(InvalidFeedCommentContentException.class)
				.hasMessage(ErrorCode.NULL_FEED_COMMENT_CONTENT.getDescription());
	}

	@Test
	@DisplayName("설명이 1000자 초과이면 InvalidFeedCommentContentException이 발생한다.")
	void throwInvalidFeedCommentContentExceptionIfFeedCommentContentLengthIs1000Over() {
		assertThatCode(() -> new FeedCommentContent("1234567890".repeat(101)))
				.isInstanceOf(InvalidFeedCommentContentException.class)
				.hasMessage(ErrorCode.INVALID_FEED_COMMENT_CONTENT_LENGTH.getDescription());
	}
}
