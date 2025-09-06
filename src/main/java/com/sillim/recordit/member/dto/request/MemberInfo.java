package com.sillim.recordit.member.dto.request;

import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import lombok.Builder;

@Builder
public record MemberInfo(
		String oauthAccount,
		OAuthProvider oAuthProvider,
		String name,
		String email,
		String profileImageUrl) {

	public Member toMember() {
		return Member.createNoJobMember(oauthAccount, oAuthProvider, name, email, profileImageUrl);
	}
}
