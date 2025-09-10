package com.sillim.recordit.feed.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedLike;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedLikeRepository;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedLikeServiceTest {

	@Mock FeedLikeRepository feedLikeRepository;
	@Mock FeedRepository feedRepository;
	@Mock MemberQueryService memberQueryService;
	@InjectMocks FeedLikeService feedLikeService;

	@Test
	@DisplayName("피드에 좋아요를 할 수 있다.")
	void feedLike() {
		long feedId = 1L;
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(memberId));
		given(feedRepository.findById(eq(feedId))).willReturn(Optional.of(feed));

		feedLikeService.feedLike(feedId, memberId);

		then(feed).should(times(1)).like();
		then(feedLikeRepository).should(times(1)).save(any(FeedLike.class));
	}

	@Test
	@DisplayName("피드 좋아요를 취소할 수 있다.")
	void feedUnlike() {
		long feedId = 1L;
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(memberId));
		willDoNothing().given(feed).unlike();
		given(feedRepository.findById(eq(feedId))).willReturn(Optional.of(feed));

		feedLikeService.feedUnlike(feedId, memberId);

		then(feed).should(times(1)).unlike();
		then(feedLikeRepository)
				.should(times(1))
				.deleteByFeedIdAndMemberId(eq(feedId), eq(memberId));
	}
}
