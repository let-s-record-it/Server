package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {}
