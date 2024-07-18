package com.sillim.recordit.feed.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedScrap;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.feed.repository.FeedScrapRepository;
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
class FeedScrapServiceTest {

	@Mock FeedScrapRepository feedScrapRepository;
	@Mock FeedRepository feedRepository;
	@Mock MemberQueryService memberQueryService;
	@InjectMocks FeedScrapService feedScrapService;

	@Test
	@DisplayName("피드에 스크랩을 할 수 있다.")
	void feedLike() {
		long feedId = 1L;
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(member));
		given(feedRepository.findById(eq(feedId))).willReturn(Optional.of(feed));

		feedScrapService.feedScrap(feedId, memberId);

		then(feedScrapRepository).should(times(1)).save(any(FeedScrap.class));
	}

	@Test
	@DisplayName("피드 스크랩를 취소할 수 있다.")
	void feedUnScrap() {
		long feedId = 1L;
		long memberId = 1L;

		feedScrapService.feedUnScrap(feedId, memberId);

		then(feedScrapRepository)
				.should(times(1))
				.deleteByFeedIdAndMemberId(eq(feedId), eq(memberId));
	}
}
