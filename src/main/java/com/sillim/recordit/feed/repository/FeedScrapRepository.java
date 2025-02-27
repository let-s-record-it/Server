package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.FeedScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedScrapRepository extends JpaRepository<FeedScrap, Long> {

	void deleteByFeedIdAndMemberId(Long feedId, Long memberId);

	boolean existsByFeedIdAndMemberId(Long feedId, Long memberId);

	@Modifying(clearAutomatically = true)
	@Query("update FeedScrap fs set fs.member = null where fs.member.id = :memberId")
	void updateMemberIsNull(@Param("memberId") Long memberId);
}
