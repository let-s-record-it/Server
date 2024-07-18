package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.FeedScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedScrapRepository extends JpaRepository<FeedScrap, Long> {

	void deleteByFeedIdAndMemberId(Long feedId, Long memberId);

	boolean existsByFeedIdAndMemberId(Long feedId, Long memberId);
}
