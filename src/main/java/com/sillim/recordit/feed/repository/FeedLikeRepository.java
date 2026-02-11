package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

	void deleteByFeedIdAndMemberId(Long feedId, Long memberId);

	boolean existsByFeedIdAndMemberId(Long feedId, Long memberId);
}
