package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedQueryServiceTest {

	@Mock FeedRepository feedRepository;
	@InjectMocks FeedQueryService feedQueryService;

	@Test
	@DisplayName("피드 id를 통해 피드를 조회할 수 있다.")
	void searchFeedById() {
		long feedId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(member));
		given(feed.getId()).willReturn(feedId);
		given(feedRepository.findByIdWithFetchJoin(eq(feedId))).willReturn(Optional.of(feed));

		Feed foundFeed = feedQueryService.searchById(feedId);

		assertThat(foundFeed.getId()).isEqualTo(feed.getId());
	}
}
