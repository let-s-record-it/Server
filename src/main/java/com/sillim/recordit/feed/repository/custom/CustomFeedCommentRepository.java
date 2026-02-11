package com.sillim.recordit.feed.repository.custom;

import com.sillim.recordit.feed.domain.FeedComment;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFeedCommentRepository {

	Slice<FeedComment> findPaginatedOrderByCreatedAtAsc(Pageable pageable, Long feedId);

	Slice<FeedComment> findByMemberIdOrderByCreatedAtAsc(Pageable pageable, Long memberId);

	Optional<FeedComment> findCommentById(Long commentId);
}
