package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.feed.repository.custom.CustomFeedRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long>, CustomFeedRepository {}
