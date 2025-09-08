package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedComment;
import com.sillim.recordit.feed.dto.request.FeedCommentAddRequest;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedCommentRepository;
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
class FeedCommentCommandServiceTest {

	@Mock
	FeedCommentRepository feedCommentRepository;
	@Mock
	FeedRepository feedRepository;
	@Mock
	MemberQueryService memberQueryService;
	@InjectMocks
	FeedCommentCommandService feedCommentCommandService;

	@Test
	@DisplayName("피드 댓글을 추가할 수 있다.")
	void addFeedComment() {
		FeedCommentAddRequest request = new FeedCommentAddRequest("content");
		long feedId = 1L;
		long feedCommentId = 1L;
		long memberId = 1L;
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		Member commentWriter = MemberFixture.DEFAULT.getMember();
		FeedComment feedComment = spy(new FeedComment("content", feed, memberId));
		given(feedComment.getId()).willReturn(feedCommentId);
		given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));
		given(feedCommentRepository.save(any(FeedComment.class))).willReturn(feedComment);

		Long savedFeedCommentId = feedCommentCommandService.addFeedComment(request, feedId, memberId);

		assertThat(savedFeedCommentId).isEqualTo(feedCommentId);
	}

	@Test
	@DisplayName("피드 댓글을 삭제할 수 있다.")
	void removeFeedComment() {
		long feedCommentId = 1L;
		long memberId = 1L;
		Feed feed = FeedFixture.DEFAULT.getFeed(memberId);
		FeedComment feedComment = spy(new FeedComment("content", feed, memberId));
		given(feedCommentRepository.findByIdWithFetch(eq(feedCommentId))).willReturn(Optional.of(feedComment));

		feedCommentCommandService.removeFeedComment(feedCommentId, memberId);

		then(feedComment).should(times(1)).delete();
	}
}
