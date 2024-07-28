package com.sillim.recordit.feed.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
class FeedRepositoryTest {

	@Autowired FeedRepository feedRepository;
	@Autowired TestEntityManager em;

	Member member;

	@BeforeEach
	void initObjects() {
		member = em.persist(MemberFixture.DEFAULT.getMember());
	}

	@Test
	@DisplayName("Feed 저장 시 FeedImage는 연관관계로 인해 같이 persist된다.")
	void test() {
		Feed feed = feedRepository.save(FeedFixture.DEFAULT.getFeed(member));

		em.flush();
		em.clear();
		Optional<Feed> foundFeed = feedRepository.findByIdWithFetchJoin(feed.getId());

		assertAll(
				() -> {
					assertThat(foundFeed).isNotEmpty();
					assertThat(foundFeed.get().getFeedImages().getFeedImageCount()).isEqualTo(1);
					assertThat(foundFeed.get().getFeedImages().getFeedImages()).hasSize(1);
					assertThat(foundFeed.get().getFeedImages().getFeedImages().get(0).getImageUrl())
							.isEqualTo("https://image.url");
				});
	}
}
