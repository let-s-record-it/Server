package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.feed.repository.FeedScrapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedScrapQueryServiceTest {

	@Mock
	FeedScrapRepository feedScrapRepository;
	@InjectMocks
	FeedScrapQueryService feedScrapQueryService;

	@Test
	@DisplayName("스크랩 여부를 확인할 수 있다.")
	void searchIsScraped() {
		long feedId = 1L;
		long memberId = 1L;
		given(feedScrapRepository.existsByFeedIdAndMemberId(eq(feedId), eq(memberId))).willReturn(true);

		boolean liked = feedScrapQueryService.isScraped(feedId, memberId);

		assertThat(liked).isTrue();
	}
}
