package com.sillim.recordit.feed.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedCommentTest {

	@Test
	@DisplayName("피드를 생성할 수 있다.")
	void createFeed() {
		Member member = MemberFixture.DEFAULT.getMember();
		Feed feed = FeedFixture.DEFAULT.getFeed(member);
		FeedComment feedComment = new FeedComment("content", feed, member);

		assertThat(feedComment.getContent()).isEqualTo("content");
	}

	@Test
	@DisplayName("피드 댓글 작성자가 아니면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotFeedCommentOwner() {
		Member member = mock(Member.class);
		Feed feed = FeedFixture.DEFAULT.getFeed(member);
		FeedComment feedComment = new FeedComment("content", feed, member);
		given(member.equalsId(anyLong())).willReturn(false);

		assertThatCode(() -> feedComment.validateAuthenticatedUser(1L))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}
}
