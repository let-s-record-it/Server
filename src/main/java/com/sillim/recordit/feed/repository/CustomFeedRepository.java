package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.Feed;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFeedRepository {

	Optional<Feed> findByIdWithFetchJoin(Long feedId);

	Slice<Feed> findPaginatedOrderByCreatedAtDesc(Pageable pageable);
}
