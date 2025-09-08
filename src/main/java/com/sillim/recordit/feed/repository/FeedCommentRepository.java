package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long>, CustomFeedCommentRepository {
}
