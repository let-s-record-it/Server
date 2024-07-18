package com.sillim.recordit.feed.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.feed.InvalidFeedLikeException;
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

	@Test
	@DisplayName("피드 좋아요 개수를 증가 시킬 수 있다.")
	void feedLikeCountUp() {
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = FeedFixture.DEFAULT.getFeed(member);

		feed.like();

		assertThat(feed.getLikeCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("피드 좋아요 개수를 감소 시킬 수 있다.")
	void feedLikeCountDown() {
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = FeedFixture.DEFAULT.getFeed(member);

		feed.like();
		feed.like();
		feed.unlike();

		assertThat(feed.getLikeCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("피드 좋아요 개수가 0 이하일 때 감소시키면 InvalidFeedLikeException이 발생한다.")
	void throwInvalidFeedLikeExceptionIfDecreaseLikeCountWhenLikeCountIs0() {
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = FeedFixture.DEFAULT.getFeed(member);

		assertThatCode(feed::unlike)
				.isInstanceOf(InvalidFeedLikeException.class)
				.hasMessage(ErrorCode.INVALID_FEED_UNLIKE.getDescription());
	}
}
