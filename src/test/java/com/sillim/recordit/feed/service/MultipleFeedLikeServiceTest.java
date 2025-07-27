package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.response.FeedDetailsResponse;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.feed.repository.FeedRepository;
import com.sillim.recordit.global.lock.RedisLockUtil;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MultipleFeedLikeServiceTest {

	@Autowired MemberRepository memberRepository;
	@Autowired FeedRepository feedRepository;

	@Autowired FeedLikeService feedLikeService;
	@Autowired FeedQueryService feedQueryService;
	@Autowired RedisLockUtil redisLockUtil;

	List<Member> members = new ArrayList<>();
	Feed feed;

	@BeforeEach
	void initObjects() {
		for (int i = 1; i <= 100; i++) {
			members.add(
					memberRepository.save(MemberFixture.DEFAULT.getMember(i + "member@mail.com")));
		}
		feed = feedRepository.save(FeedFixture.DEFAULT.getFeed(members.get(0)));
	}

	@Test
	@DisplayName("좋아요를 빠르게 2번 눌러도 한 번만 적용된다.")
	void multipleFeedLike() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			long feedId = feed.getId();
			String key = String.valueOf(feedId);
			executorService.submit(
					() -> {
						try {
							redisLockUtil.acquireAndRunLock(
									key,
									() -> {
										feedLikeService.feedLike(feedId, members.get(0).getId());
										return true;
									});
						} finally {
							latch.countDown();
						}
					});
		}

		latch.await();

		FeedDetailsResponse feed =
				feedQueryService.searchById(this.feed.getId(), members.get(0).getId());

		assertThat(feed.likeCount()).isEqualTo(1);
	}
}
