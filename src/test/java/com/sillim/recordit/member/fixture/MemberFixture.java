package com.sillim.recordit.member.fixture;

import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import java.time.LocalDateTime;
import java.util.List;

public enum MemberFixture {
	DEFAULT("123456", OAuthProvider.KAKAO, "홍길동", "개발자", "test@mail.com", "https://image.url", 0L, 0L, false, true,
			LocalDateTime.now(), LocalDateTime.now(), List.of(MemberRole.ROLE_USER)),;

	private final String oauthAccount;
	private final OAuthProvider oAuthProvider;
	private final String name;
	private final String job;
	private final String email;
	private final String profileImageUrl;
	private final Long follower;
	private final Long following;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;
	private final Boolean deleted;
	private final Boolean activated;
	private final List<MemberRole> memberRole;

	MemberFixture(String oauthAccount, OAuthProvider oAuthProvider, String name, String job, String email,
			String profileImageUrl, Long follower, Long following, Boolean deleted, Boolean activated,
			LocalDateTime createdAt, LocalDateTime modifiedAt, List<MemberRole> memberRole) {
		this.oauthAccount = oauthAccount;
		this.oAuthProvider = oAuthProvider;
		this.name = name;
		this.job = job;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.follower = follower;
		this.following = following;
		this.deleted = deleted;
		this.activated = activated;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.memberRole = memberRole;
	}

	public Member getMember() {
		return Member.builder().oauthAccount(this.oauthAccount).oauthProvider(this.oAuthProvider).name(this.name)
				.job(this.job).email(this.email).profileImageUrl(this.profileImageUrl).followerCount(this.follower)
				.followingCount(this.following).deleted(this.deleted).activated(this.activated)
				.createdAt(this.createdAt).modifiedAt(this.modifiedAt).memberRole(this.memberRole).build();
	}

	public Member getMember(String email) {
		return Member.builder().oauthAccount(this.oauthAccount).oauthProvider(this.oAuthProvider).name(this.name)
				.job(this.job).email(email).profileImageUrl(this.profileImageUrl).followerCount(this.follower)
				.followingCount(this.following).deleted(this.deleted).activated(this.activated)
				.createdAt(this.createdAt).modifiedAt(this.modifiedAt).memberRole(this.memberRole).build();
	}
}
