package com.sillim.recordit.feed.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedImageTest {

	@Test
	@DisplayName("피드 이미지를 생성할 수 있다.")
	void addFeedImage() {
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = FeedFixture.DEFAULT.getFeed(member);
		FeedImage feedImage = new FeedImage("https://image.url", feed);

		assertThat(feedImage.getImageUrl()).isEqualTo("https://image.url");
	}
}
