package com.sillim.recordit.feed.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.dto.response.FeedDetailsResponse;
import com.sillim.recordit.feed.facade.FeedLikeFacade;
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
	@Autowired FeedLikeFacade feedLikeFacade;
	@Autowired FeedQueryService feedQueryService;

	@Test
	@DisplayName("좋아요를 빠르게 여러 번 눌러도 한 번만 적용된다.")
	void multipleFeedLike() throws InterruptedException {
		Member member;
		Feed feed;

		member = memberRepository.save(MemberFixture.DEFAULT.getMember("like@mail.com"));
		feed = feedRepository.save(FeedFixture.DEFAULT.getFeed(member));

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			long feedId = feed.getId();
			String key = "like:" + feedId + ":" + member.getId();
			executorService.submit(
					() -> {
						try {
							RedisLockUtil.acquireAndRunLock(
									key,
									() -> {
										feedLikeService.feedLike(feedId, member.getId());
										return true;
									});
						} finally {
							latch.countDown();
						}
					});
		}

		latch.await();

		FeedDetailsResponse feedResponse =
				feedQueryService.searchById(feed.getId(), member.getId());

		assertThat(feedResponse.likeCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("좋아요 개수 동시성 문제 체크")
	void multipleFeedLikeByPeople() throws InterruptedException {
		List<Member> members = new ArrayList<>();
		Feed feed;
		for (int i = 1; i <= 100; i++) {
			members.add(
					memberRepository.save(MemberFixture.DEFAULT.getMember(i + "member@mail.com")));
		}
		feed = feedRepository.save(FeedFixture.DEFAULT.getFeed(members.get(0)));

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			long feedId = feed.getId();
			long memberId = members.get(i).getId();
			executorService.submit(
					() -> {
						try {
							feedLikeFacade.feedLikeRetry(feedId, memberId, 20);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						} finally {
							latch.countDown();
						}
					});
		}

		latch.await();

		FeedDetailsResponse feedResponse =
				feedQueryService.searchById(feed.getId(), members.get(0).getId());

		assertThat(feedResponse.likeCount()).isEqualTo(100);
	}
}
