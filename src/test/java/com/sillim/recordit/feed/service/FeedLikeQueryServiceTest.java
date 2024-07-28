package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.feed.repository.FeedLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedLikeQueryServiceTest {

	@Mock FeedLikeRepository feedLikeRepository;
	@InjectMocks FeedLikeQueryService feedLikeQueryService;

	@Test
	@DisplayName("좋아요 여부를 확인할 수 있다.")
	void searchIsLiked() {
		long feedId = 1L;
		long memberId = 1L;
		given(feedLikeRepository.existsByFeedIdAndMemberId(eq(feedId), eq(memberId)))
				.willReturn(true);

		boolean liked = feedLikeQueryService.isLiked(feedId, memberId);

		assertThat(liked).isTrue();
	}
}
