package com.sillim.recordit.member.dto.request;

import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import lombok.Builder;

@Builder
public record MemberInfo(String oauthAccount, OAuthProvider oAuthProvider, String name, String profileImageUrl) {

	public Member toMember() {
		return Member.createNoJobMember(new Auth(oauthAccount, oAuthProvider), name, profileImageUrl);
	}
}
