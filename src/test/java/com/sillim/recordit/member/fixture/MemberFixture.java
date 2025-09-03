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
			"test@mail.com",
			"https://image.url",
			0L,
			0L,
			false,
			true,
			List.of(MemberRole.ROLE_USER)),
	;

	private final Auth auth;
	private final String name;
	private final String job;
	private final String email;
	private final String profileImageUrl;
	private final Long follower;
	private final Long following;
	private final Boolean deleted;
	private final Boolean activated;
	private final List<MemberRole> memberRole;

	MemberFixture(
			Auth auth,
			String name,
			String job,
			String email,
			String profileImageUrl,
			Long follower,
			Long following,
			Boolean deleted,
			Boolean activated,
			List<MemberRole> memberRole) {
		this.auth = auth;
		this.name = name;
		this.job = job;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.follower = follower;
		this.following = following;
		this.deleted = deleted;
		this.activated = activated;
		this.memberRole = memberRole;
	}

	public Member getMember() {
		return Member.builder()
				.auth(this.auth)
				.name(this.name)
				.job(this.job)
				.email(this.email)
				.profileImageUrl(this.profileImageUrl)
				.follower(this.follower)
				.following(this.following)
				.deleted(this.deleted)
				.activated(this.activated)
				.memberRole(this.memberRole)
				.build();
	}

	public Member getMember(String email) {
		return Member.builder()
				.auth(this.auth)
				.name(this.name)
				.job(this.job)
				.email(email)
				.profileImageUrl(this.profileImageUrl)
				.follower(this.follower)
				.following(this.following)
				.deleted(this.deleted)
				.activated(this.activated)
				.memberRole(this.memberRole)
				.build();
	}
}
