package com.sillim.recordit.feed.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedTest {

	@Test
	@DisplayName("피드를 생성할 수 있다.")
	void createFeed() {
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = FeedFixture.DEFAULT.getFeed(member);
		FeedFixture feedFixture = FeedFixture.DEFAULT;

		assertAll(
				() -> {
					assertThat(feed.getTitle()).isEqualTo(feedFixture.getTitle());
					assertThat(feed.getContent()).isEqualTo(feedFixture.getContent());
				});
	}
}
