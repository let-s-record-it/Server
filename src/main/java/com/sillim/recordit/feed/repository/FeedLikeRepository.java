package com.sillim.recordit.feed.repository;

import com.sillim.recordit.feed.domain.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

	void deleteByFeedIdAndMemberId(Long feedId, Long memberId);

	boolean existsByFeedIdAndMemberId(Long feedId, Long memberId);

	@Modifying(clearAutomatically = true)
	@Query("update FeedLike fl set fl.memberId = null where fl.memberId = :memberId")
	void updateMemberIsNull(@Param("memberId") Long memberId);
}
