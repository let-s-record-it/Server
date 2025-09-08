package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.rabbitmq.service.MessagePublisher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FeedCommandServiceTest {

	@Mock
	FeedRepository feedRepository;
	@Mock
	MemberQueryService memberQueryService;
	@Mock
	MessagePublisher messagePublisher;
	@InjectMocks
	FeedCommandService feedCommandService;

	@Test
	@DisplayName("피드를 추가할 수 있다.")
	void addFeed() throws IOException {
		Member member = mock(Member.class);
		Feed feed = mock(Feed.class);
		MockMultipartFile multipartFile = new MockMultipartFile("images", "image.jpg", "text/plain",
				"test1".getBytes(StandardCharsets.UTF_8));
		given(feed.getId()).willReturn(1L);
		given(feedRepository.save(any(Feed.class))).willReturn(feed);

		FeedAddRequest feedAddRequest = new FeedAddRequest("title", "content");
		Long feedId = feedCommandService.addFeed(feedAddRequest, List.of(multipartFile), 1L);

		assertThat(feedId).isEqualTo(1L);
	}

	@Test
	@DisplayName("피드를 지울 수 있다.")
	void removeFeed() {
		long feedId = 1L;
		long memberId = 1L;
		Feed feed = spy(FeedFixture.DEFAULT.getFeed(memberId));
		given(feedRepository.findById(eq(feedId))).willReturn(Optional.of(feed));

		feedCommandService.removeFeed(feedId);

		then(feed).should(times(1)).delete();
	}
}
