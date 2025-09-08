package com.sillim.recordit.feed.repository;

import static com.sillim.recordit.feed.domain.QFeed.feed;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CustomFeedRepositoryImpl extends QuerydslRepositorySupport implements CustomFeedRepository {

	public CustomFeedRepositoryImpl() {
		super(Feed.class);
	}

	@Override
	public Optional<Feed> findByIdWithFetchJoin(Long feedId) {
		return Optional.ofNullable(selectFrom(feed).leftJoin(feed.feedImages.feedImages).fetchJoin()
				// .leftJoin(feed.member)
				// .fetchJoin()
				.where(feed.deleted.isFalse()).where(feed.id.eq(feedId)).fetchOne());
	}

	@Override
	public Slice<Feed> findOrderByCreatedAtDesc(Pageable pageable) {
		List<Feed> feeds = selectFrom(feed)
				// .leftJoin(feed.member)
				// .fetchJoin()
				.where(feed.deleted.isFalse()).orderBy(feed.createdAt.desc()).offset(pageable.getOffset())
				.limit(pageable.getPageSize() + 1).fetch();

		return new SliceImpl<>(feeds, pageable, hasNext(pageable, feeds));
	}

	@Override
	public Slice<Feed> findByMemberIdOrderByCreatedAtDesc(Pageable pageable, Long memberId) {
		List<Feed> feeds = selectFrom(feed)
				// .leftJoin(feed.member)
				// .fetchJoin()
				.where(feed.deleted.isFalse().and(feed.memberId.eq(memberId))).orderBy(feed.createdAt.desc())
				.offset(pageable.getOffset()).limit(pageable.getPageSize() + 1).fetch();

		return new SliceImpl<>(feeds, pageable, hasNext(pageable, feeds));
	}

	@Override
	public void updateMemberIsNull(Long memberId) {
		update(feed).setNull(feed.memberId).set(feed.deleted, true).where(feed.memberId.eq(memberId)).execute();
	}
}
