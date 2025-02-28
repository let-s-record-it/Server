package com.sillim.recordit.member.dto.response;

import com.sillim.recordit.member.domain.Member;

public record ProfileResponse(
		Long id, String name, String job, String email, String profileImageUrl) {

	public static ProfileResponse of(Member member) {
		return new ProfileResponse(
				member.getId(),
				member.getName(),
				member.getJob(),
				member.getEmail(),
				member.getProfileImageUrl());
	}
}
