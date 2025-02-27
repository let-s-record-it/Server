package com.sillim.recordit.config.security.oauth2;

import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import java.util.List;

public record OAuth2Request(
		String account, OAuthProvider provider, String name, String imageUrl, String email) {

	public Member toMember(String job) {
		return Member.builder()
				.auth(new Auth(account, provider))
				.name(name)
				.job(job)
				.email(email)
				.profileImageUrl(imageUrl)
				.memberRole(List.of(MemberRole.ROLE_USER))
				.build();
	}
}
