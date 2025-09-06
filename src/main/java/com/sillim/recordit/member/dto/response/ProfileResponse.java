package com.sillim.recordit.member.dto.response;

import com.sillim.recordit.member.domain.Member;

public record ProfileResponse(
		Long id,
		String name,
		String job,
		String personalId,
		String email,
		String profileImageUrl,
		Long followerCount,
		Long followingCount,
		boolean isFollowing) {

	public static ProfileResponse of(Member member, boolean isFollowing) {
		return new ProfileResponse(
				member.getId(),
				member.getName(),
				member.getJob(),
				member.getPersonalId(),
				member.getEmail(),
				member.getProfileImageUrl(),
				member.getFollowerCount(),
				member.getFollowingCount(),
				isFollowing);
	}
}
