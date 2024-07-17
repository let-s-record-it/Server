package com.sillim.recordit.feed.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
}
