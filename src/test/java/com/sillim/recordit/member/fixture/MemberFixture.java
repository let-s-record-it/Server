package com.sillim.recordit.member.fixture;

import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import java.util.List;

public enum MemberFixture {
	DEFAULT(
			new Auth("123456", OAuthProvider.KAKAO),
			"홍길동",
			"개발자",
			"https://image.url",
			false,
			List.of(MemberRole.ROLE_USER)),
	;

	private final Auth auth;
	private final String name;
	private final String job;
	private final String profileImageUrl;
	private final Boolean deleted;
	private final List<MemberRole> memberRole;

	MemberFixture(
			Auth auth, String name, String job, String profileImageUrl, Boolean deleted, List<MemberRole> memberRole) {
		this.auth = auth;
		this.name = name;
		this.job = job;
		this.profileImageUrl = profileImageUrl;
		this.deleted = deleted;
		this.memberRole = memberRole;
	}

	public Member getMember() {
		return Member.builder()
				.auth(this.auth)
				.name(this.name)
				.job(this.job)
				.profileImageUrl(this.profileImageUrl)
				.deleted(this.deleted)
				.memberRole(this.memberRole)
				.build();
	}
}
