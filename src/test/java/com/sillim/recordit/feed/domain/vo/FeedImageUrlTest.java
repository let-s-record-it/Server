package com.sillim.recordit.feed.domain.vo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedImageUrlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedImageUrlTest {

	@Test
	@DisplayName("FeedImageUrl이 null이면 InvalidFeedImageUrlException이 발생한다.")
	void throwInvalidFeedImageUrlExceptionIfFeedImageUrlIsNull() {
		assertThatCode(() -> new FeedImageUrl(null)).isInstanceOf(InvalidFeedImageUrlException.class)
				.hasMessage(ErrorCode.NULL_FEED_IMAGE_URL.getDescription());
	}

	@Test
	@DisplayName("FeedImageUrl이 공백이면 InvalidFeedImageUrlException이 발생한다.")
	void throwInvalidFeedImageUrlExceptionIfImageUrlIsBlank() {
		assertThatCode(() -> new FeedImageUrl("          ")).isInstanceOf(InvalidFeedImageUrlException.class)
				.hasMessage(ErrorCode.BLANK_FEED_IMAGE_URL.getDescription());
	}
}
