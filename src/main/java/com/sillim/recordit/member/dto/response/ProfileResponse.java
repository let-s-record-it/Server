package com.sillim.recordit.member.dto.response;

import com.sillim.recordit.member.domain.Member;

public record ProfileResponse(
		Long id, String name, String job, String personalId, String email, String profileImageUrl, Long follower, Long following) {

	public static ProfileResponse of(Member member) {
		return new ProfileResponse(
				member.getId(),
				member.getName(),
				member.getJob(),
				member.getPersonalId(),
				member.getEmail(),
				member.getProfileImageUrl(),
				member.getFollower(),
				member.getFollowing());
	}
}
