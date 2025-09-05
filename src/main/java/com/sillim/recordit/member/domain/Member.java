package com.sillim.recordit.member.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Node("Member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	public static final int DO_NOT_REJOIN_DAYS = 14;

	@Id @GeneratedValue private Long id;

	private String oauthAccount;

	private OAuthProvider oauthProvider;

	private String personalId;

	private String name;

	private String job;

	private String email;

	private String profileImageUrl;

	private Long followerCount;

	private Long followingCount;

	private Boolean deleted;

	private Boolean activated;

	private LocalDateTime deletedTime;

	private LocalDateTime createdAt;

	private LocalDateTime modifiedAt;

	private List<MemberRole> memberRole;

	@Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
	private List<Member> followings;

	@Relationship(type = "FOLLOWS", direction = Relationship.Direction.INCOMING)
	private List<Member> followers;

	@Builder
	public Member(
			String oauthAccount,
			OAuthProvider oauthProvider,
			String personalId,
			String name,
			String job,
			String email,
			String profileImageUrl,
			Long followerCount,
			Long followingCount,
			Boolean deleted,
			Boolean activated,
			LocalDateTime createdAt,
			LocalDateTime modifiedAt,
			LocalDateTime deletedTime,
			List<Member> followings,
			List<MemberRole> memberRole) {
		this.oauthAccount = oauthAccount;
		this.oauthProvider = oauthProvider;
		this.name = name;
		this.job = job;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.followerCount = followerCount;
		this.followingCount = followingCount;
		this.deleted = deleted;
		this.activated = activated;
		this.memberRole = memberRole;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.personalId = personalId;
		this.followings = followings;
		this.deletedTime = deletedTime;
	}

	public static Member createNoJobMember(
			String oauthAccount,
			OAuthProvider oauthProvider,
			String name,
			String email,
			String profileImageUrl) {
		return Member.builder()
				.oauthAccount(oauthAccount)
				.oauthProvider(oauthProvider)
				.name(name)
				.job("")
				.email(email)
				.profileImageUrl(profileImageUrl)
				.followerCount(0L)
				.followingCount(0L)
				.createdAt(LocalDateTime.now())
				.modifiedAt(LocalDateTime.now())
				.deleted(false)
				.activated(false)
				.followings(List.of())
				.memberRole(List.of(MemberRole.ROLE_USER))
				.build();
	}

	public List<SimpleGrantedAuthority> getAuthorities() {
		return memberRole.stream().map(MemberRole::name).map(SimpleGrantedAuthority::new).toList();
	}

	public boolean equalsId(Long id) {
		return this.id.equals(id);
	}

	public void modifyInfo(String name, String job) {
		this.name = name;
		this.job = job;
	}

	public void modifyProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void followed() {
		this.followerCount++;
	}

	public void unfollowed() {
		this.followerCount--;
	}

	public void follow() {
		this.followingCount++;
	}

	public void unfollow() {
		this.followingCount--;
	}

	public void delete() {
		this.deleted = true;
		this.deletedTime = LocalDateTime.now();
	}

	public void active(String personalId) {
		this.personalId = personalId;
		this.activated = true;
	}

	public boolean isCanRejoin() {
		return this.deletedTime.plusDays(DO_NOT_REJOIN_DAYS).isBefore(LocalDateTime.now());
	}
}
