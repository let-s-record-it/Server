package com.sillim.recordit.member.dto.request;

import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import java.util.List;
import lombok.Builder;

@Builder
public record SignupRequest(String oauthAccount, OAuthProvider oAuthProvider, String name) {

	public Member toMember() {
		return Member.builder()
				.auth(new Auth(oauthAccount, oAuthProvider))
				.name(name)
				.job("")
				.deleted(false)
				.memberRole(List.of(MemberRole.ROLE_USER))
				.build();
	}
}
