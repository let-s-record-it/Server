package com.sillim.recordit.feed.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.domain.FeedComment;
import com.sillim.recordit.feed.fixture.FeedFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.util.List;
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
class CustomFeedCommentRepositoryTest {

	@Qualifier("customFeedCommentRepositoryImpl") @Autowired
	CustomFeedCommentRepository customFeedCommentRepository;

	@Autowired TestEntityManager em;

	long memberId = 1L;
	Member member;
	Feed feed;

	@BeforeEach
	void initObjects() {
		member = MemberFixture.DEFAULT.getMember();
		feed = em.persist(FeedFixture.DEFAULT.getFeed(memberId));
	}

	@Test
	@DisplayName("피드 댓글을 pagination해서 created 오름차순으로 조회한다.")
	void findPaginatedOrderByCreatedDesc() {
		List<FeedComment> feedComments =
				IntStream.range(0, 10)
						.mapToObj(i -> em.persist(new FeedComment("content", feed, memberId)))
						.toList();

		Slice<FeedComment> foundFeedComments1 =
				customFeedCommentRepository.findPaginatedOrderByCreatedAtAsc(
						PageRequest.of(0, 5), feed.getId());

		Slice<FeedComment> foundFeedComments2 =
				customFeedCommentRepository.findPaginatedOrderByCreatedAtAsc(
						PageRequest.of(3, 3), feed.getId());

		assertAll(
				() -> {
					assertThat(foundFeedComments1).hasSize(5);
					assertThat(foundFeedComments1.isLast()).isFalse();
					assertThat(foundFeedComments1.getContent().get(0).getId())
							.isEqualTo(feedComments.get(0).getId());
					assertThat(foundFeedComments2).hasSize(1);
					assertThat(foundFeedComments2.isLast()).isTrue();
				});
	}

	@Test
	@DisplayName("특정 멤버의 피드 댓글을 pagination해서 created 오름차순으로 조회한다.")
	void findPaginatedByMemberIdOrderByCreatedDesc() {
		List<FeedComment> feedComments =
				IntStream.range(0, 10)
						.mapToObj(i -> em.persist(new FeedComment("content", feed, memberId)))
						.toList();

		Slice<FeedComment> foundFeedComments1 =
				customFeedCommentRepository.findByMemberIdOrderByCreatedAtAsc(
						PageRequest.of(0, 5), memberId);

		Slice<FeedComment> foundFeedComments2 =
				customFeedCommentRepository.findByMemberIdOrderByCreatedAtAsc(
						PageRequest.of(3, 3), memberId);

		assertAll(
				() -> {
					assertThat(foundFeedComments1).hasSize(5);
					assertThat(foundFeedComments1.isLast()).isFalse();
					assertThat(foundFeedComments1.getContent().get(0).getCreatedAt())
							.isBefore(foundFeedComments2.getContent().get(0).getCreatedAt());
					assertThat(foundFeedComments2).hasSize(1);
					assertThat(foundFeedComments2.isLast()).isTrue();
				});
	}
}
