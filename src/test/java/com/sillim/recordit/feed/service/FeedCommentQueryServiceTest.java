package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedComment;
import com.sillim.recordit.feed.dto.response.FeedCommentInListResponse;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedCommentRepository;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class FeedCommentQueryServiceTest {

	@Mock
	FeedCommentRepository feedCommentRepository;
	@Mock
	MemberQueryService memberQueryService;
	@InjectMocks
	FeedCommentQueryService feedCommentQueryService;

	@Test
	@DisplayName("오래된 순으로 pagination해서 피드 댓글 목록을 조회할 수 있다.")
	void searchFeedCommentsPaginatedOrderByRecentCreated() {
		long feedId = 1L;
		long memberId = 1L;
		PageRequest pageRequest = PageRequest.of(1, 10);
		Member member = mock(Member.class);
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		FeedComment feedComment = spy(new FeedComment("content", feed, memberId));
		given(feedComment.getId()).willReturn(1L);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(feedCommentRepository.findPaginatedOrderByCreatedAtAsc(eq(pageRequest), eq(feedId)))
				.willReturn(new SliceImpl<>(List.of(feedComment), pageRequest, false));

		SliceResponse<FeedCommentInListResponse> feedComments = feedCommentQueryService
				.searchPaginatedOldCreated(pageRequest, feedId, 1L);

		assertAll(() -> {
			assertThat(feedComments.content()).hasSize(1);
			assertThat(feedComments.isLast()).isTrue();
		});
	}

	@Test
	@DisplayName("오래된 순으로 pagination해서 특정 멤버의 피드 댓글 목록을 조회할 수 있다.")
	void searchFeedCommentsPaginatedByMemberIdOrderByRecentCreated() {
		long memberId = 1L;
		PageRequest pageRequest = PageRequest.of(1, 10);
		Member member = mock(Member.class);
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		FeedComment feedComment = spy(new FeedComment("content", feed, memberId));
		given(feedComment.getId()).willReturn(1L);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(feedCommentRepository.findByMemberIdOrderByCreatedAtAsc(eq(pageRequest), eq(memberId)))
				.willReturn(new SliceImpl<>(List.of(feedComment), pageRequest, false));

		SliceResponse<FeedCommentInListResponse> feedComments = feedCommentQueryService
				.searchByMemberIdOldCreated(pageRequest, memberId);

		assertAll(() -> {
			assertThat(feedComments.content()).hasSize(1);
			assertThat(feedComments.isLast()).isTrue();
		});
	}
}
