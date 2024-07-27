package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.FeedComment;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFeedCommentRepository {

	Slice<FeedComment> findPaginatedOrderByCreatedAtAsc(Pageable pageable, Long feedId);

	Optional<FeedComment> findByIdWithFetch(Long commentId);
}
