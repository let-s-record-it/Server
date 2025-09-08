package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.response.FeedDetailsResponse;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.lock.RedisLockUtil;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.repository.MemberRepository;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MultipleFeedLikeServiceTest {

	@MockBean
	MemberRepository memberRepository;
	@Autowired
	FeedRepository feedRepository;

	@Autowired
	FeedLikeService feedLikeService;
	@Autowired
	FeedQueryService feedQueryService;

	@Test
	@DisplayName("좋아요를 빠르게 여러 번 눌러도 한 번만 적용된다.")
	void multipleFeedLike() throws InterruptedException {
		long memberId = 1L;
		Feed feed;
		Member member = Mockito.mock(Member.class);
		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		feed = feedRepository.save(FeedFixture.DEFAULT.getFeed(memberId));

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			long feedId = feed.getId();
			String key = "like:" + feedId + ":" + memberId;
			executorService.submit(() -> {
				try {
					RedisLockUtil.acquireAndRunLock(key, () -> {
						feedLikeService.feedLike(feedId, memberId);
						return true;
					});
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		FeedDetailsResponse feedResponse = feedQueryService.searchById(feed.getId(), memberId);

		assertThat(feedResponse.likeCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("좋아요 개수 동시성 문제 체크")
	void multipleFeedLikeByPeople() throws InterruptedException {
		Feed feed;
		Member member = Mockito.mock(Member.class);
		given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
		feed = feedRepository.save(FeedFixture.DEFAULT.getFeed(1L));

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			long feedId = feed.getId();
			long memberId = i + 1L;
			executorService.submit(() -> {
				try {
					feedLikeService.feedLike(feedId, memberId);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		FeedDetailsResponse feedResponse = feedQueryService.searchById(feed.getId(), 1L);

		assertThat(feedResponse.likeCount()).isEqualTo(100);
	}
}
