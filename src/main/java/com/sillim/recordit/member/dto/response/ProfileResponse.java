package com.sillim.recordit.member.dto.response;

import com.sillim.recordit.member.domain.Member;

public record ProfileResponse(String name, String job) {

	public static ProfileResponse of(Member member) {
		return new ProfileResponse(member.getName(), member.getJob());
	}
}
