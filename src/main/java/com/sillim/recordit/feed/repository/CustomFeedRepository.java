package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.Feed;
import java.util.Optional;

public interface CustomFeedRepository {

	Optional<Feed> findByIdWithFetchJoin(Long feedId);
}
