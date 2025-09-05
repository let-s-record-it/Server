package com.sillim.recordit.feed.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.feed.fixture.FeedFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedImageTest {

	@Test
	@DisplayName("피드 이미지를 생성할 수 있다.")
	void addFeedImage() {
		long memberId = 1L;
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		FeedImage feedImage = new FeedImage("https://image.url", feed);

		assertThat(feedImage.getImageUrl()).isEqualTo("https://image.url");
	}
}
