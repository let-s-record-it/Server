package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.response.FeedDetailsResponse;
import com.sillim.recordit.feed.dto.response.FeedInListResponse;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class FeedQueryServiceTest {

	@Mock
	FeedRepository feedRepository;
	@Mock
	FeedLikeQueryService feedLikeQueryService;
	@Mock
	FeedScrapQueryService feedScrapQueryService;
	@Mock
	MemberQueryService memberQueryService;
	@InjectMocks
	FeedQueryService feedQueryService;

	@Test
	@DisplayName("피드 id를 통해 피드를 조회할 수 있다.")
	void searchFeedById() {
		long feedId = 1L;
		long memberId = 1L;
		Member member = mock(Member.class);
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(memberId));
		given(feed.getId()).willReturn(feedId);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(feedRepository.findByIdWithFetchJoin(eq(feedId))).willReturn(Optional.of(feed));

		FeedDetailsResponse feedResponse = feedQueryService.searchById(feedId, memberId);

		assertThat(feedResponse.id()).isEqualTo(feed.getId());
	}

	@Test
	@DisplayName("최신 순으로 pagination해서 피드 목록을 조회할 수 있다.")
	void searchFeedPaginatedOrderByRecentCreated() {
		long memberId = 1L;
		PageRequest pageRequest = PageRequest.of(1, 10);
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(memberId));
		given(feed.getId()).willReturn(1L);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(feedRepository.findOrderByCreatedAtDesc(eq(pageRequest)))
				.willReturn(new SliceImpl<>(List.of(feed), pageRequest, false));
		given(feedLikeQueryService.isLiked(any(), any())).willReturn(true);
		given(feedScrapQueryService.isScraped(any(), any())).willReturn(true);

		SliceResponse<FeedInListResponse> feeds = feedQueryService.searchRecentCreated(pageRequest, 1L);

		assertAll(() -> {
			assertThat(feeds.content()).hasSize(1);
			assertThat(feeds.isLast()).isTrue();
		});
	}

	@Test
	@DisplayName("최신 순으로 pagination해서 특정 Member의 피드 목록을 조회할 수 있다.")
	void searchFeedByMemberIdPaginatedOrderByRecentCreated() {
		long memberId = 1L;
		PageRequest pageRequest = PageRequest.of(1, 10);
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(memberId));
		given(feed.getId()).willReturn(1L);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(feedRepository.findByMemberIdOrderByCreatedAtDesc(eq(pageRequest), eq(1L)))
				.willReturn(new SliceImpl<>(List.of(feed), pageRequest, false));
		given(feedLikeQueryService.isLiked(any(), any())).willReturn(true);
		given(feedScrapQueryService.isScraped(any(), any())).willReturn(true);

		SliceResponse<FeedInListResponse> feeds = feedQueryService.searchRecentCreatedByMemberId(pageRequest, 1L);

		assertAll(() -> {
			assertThat(feeds.content()).hasSize(1);
			assertThat(feeds.isLast()).isTrue();
		});
	}
}
