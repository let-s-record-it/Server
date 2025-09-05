package com.sillim.recordit.member.repository;

import com.sillim.recordit.member.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	void deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);
}
