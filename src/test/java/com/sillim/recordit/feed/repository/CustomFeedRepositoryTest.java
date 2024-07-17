package com.sillim.recordit.feed.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
class CustomFeedRepositoryTest {

	@Qualifier("customFeedRepositoryImpl") @Autowired
	CustomFeedRepository customFeedRepository;

	@Autowired TestEntityManager em;

	Member member;

	@BeforeEach
	void initObjects() {
		member = em.persist(MemberFixture.DEFAULT.getMember());
	}

	@Test
	@DisplayName("피드 id로 피드를 조회한다.")
	void findById() {
		Feed feed = em.persist(FeedFixture.DEFAULT.getFeed(member));

		Optional<Feed> foundFeed = customFeedRepository.findByIdWithFetchJoin(feed.getId());

		assertThat(foundFeed).isNotEmpty();
		assertThat(foundFeed.get().getId()).isEqualTo(feed.getId());
	}

	@Test
	@DisplayName("피드를 pagination해서 created 내림차순으로 조회한다.")
	void findPaginatedOrderByCreatedDesc() {
		List<Feed> feeds =
				IntStream.range(0, 10)
						.mapToObj(i -> em.persist(FeedFixture.DEFAULT.getFeed(member)))
						.toList();

		Slice<Feed> foundFeeds =
				customFeedRepository.findPaginatedOrderByCreatedAtDesc(PageRequest.of(0, 5));

		Slice<Feed> foundFeeds2 =
				customFeedRepository.findPaginatedOrderByCreatedAtDesc(PageRequest.of(3, 3));

		assertAll(
				() -> {
					assertThat(foundFeeds).hasSize(5);
					assertThat(foundFeeds.isLast()).isFalse();
					assertThat(foundFeeds.getContent().get(0).getId())
							.isEqualTo(feeds.get(9).getId());
					assertThat(foundFeeds2).hasSize(1);
					assertThat(foundFeeds2.isLast()).isTrue();
				});
	}
}
