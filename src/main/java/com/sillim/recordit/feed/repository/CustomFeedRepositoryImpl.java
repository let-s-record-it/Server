package com.sillim.recordit.feed.repository;

import static com.sillim.recordit.feed.domain.QFeed.feed;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomFeedRepositoryImpl extends QuerydslRepositorySupport
		implements CustomFeedRepository {

	public CustomFeedRepositoryImpl() {
		super(Feed.class);
	}

	@Override
	public Optional<Feed> findByIdWithFetchJoin(Long feedId) {
		return Optional.ofNullable(
				selectFrom(feed)
						.leftJoin(feed.feedImages.feedImages)
						.fetchJoin()
						.leftJoin(feed.member)
						.fetchJoin()
						.where(feed.id.eq(feedId))
						.fetchOne());
	}
}
