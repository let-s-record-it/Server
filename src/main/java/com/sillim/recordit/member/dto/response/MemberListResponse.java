package com.sillim.recordit.member.dto.response;

import com.sillim.recordit.member.domain.Member;

public record MemberListResponse(Long id, String personalId, String name, String profileImageUrl) {

	public static MemberListResponse of(Member member) {
		return new MemberListResponse(member.getId(), member.getPersonalId(), member.getName(),
				member.getProfileImageUrl());
	}
}
