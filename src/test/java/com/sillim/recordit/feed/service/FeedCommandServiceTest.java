package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedCommandServiceTest {

	@Mock FeedRepository feedRepository;
	@Mock MemberQueryService memberQueryService;
	@InjectMocks FeedCommandService feedCommandService;

	@Test
	@DisplayName("피드를 추가할 수 있다.")
	void addFeed() {
		Member member = mock(Member.class);
		Feed feed = mock(Feed.class);
		given(feed.getId()).willReturn(1L);
		given(feedRepository.save(any(Feed.class))).willReturn(feed);
		given(memberQueryService.findByMemberId(eq(1L))).willReturn(member);

		FeedAddRequest feedAddRequest =
				new FeedAddRequest("title", "content");
		Long feedId = feedCommandService.addFeed(feedAddRequest, 1L);

		assertThat(feedId).isEqualTo(1L);
	}
}
