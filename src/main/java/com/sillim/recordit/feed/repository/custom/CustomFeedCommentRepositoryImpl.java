package com.sillim.recordit.feed.repository.custom;

import static com.sillim.recordit.feed.domain.QFeedComment.feedComment;

import com.sillim.recordit.feed.domain.FeedComment;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CustomFeedCommentRepositoryImpl extends QuerydslRepositorySupport
		implements CustomFeedCommentRepository {

	public CustomFeedCommentRepositoryImpl() {
		super(FeedComment.class);
	}

	@Override
	public Slice<FeedComment> findPaginatedOrderByCreatedAtAsc(Pageable pageable, Long feedId) {
		List<FeedComment> feedComments =
				selectFrom(feedComment)
						.where(feedComment.deleted.isFalse(), feedComment.feed.id.eq(feedId))
						.orderBy(feedComment.createdAt.asc())
						.offset(pageable.getOffset())
						.limit(pageable.getPageSize() + 1)
						.fetch();

		return new SliceImpl<>(feedComments, pageable, hasNext(pageable, feedComments));
	}

	@Override
	public Slice<FeedComment> findByMemberIdOrderByCreatedAtAsc(Pageable pageable, Long memberId) {
		List<FeedComment> feedComments =
				selectFrom(feedComment)
						.where(feedComment.deleted.isFalse(), feedComment.memberId.eq(memberId))
						.orderBy(feedComment.createdAt.asc())
						.offset(pageable.getOffset())
						.limit(pageable.getPageSize() + 1)
						.fetch();

		return new SliceImpl<>(feedComments, pageable, hasNext(pageable, feedComments));
	}

	@Override
	public Optional<FeedComment> findCommentById(Long commentId) {
		return Optional.ofNullable(
				selectFrom(feedComment)
						.where(feedComment.deleted.isFalse(), feedComment.id.eq(commentId))
						.fetchFirst());
	}
}
