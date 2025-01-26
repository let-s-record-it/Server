package com.sillim.recordit.member.domain;

import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", nullable = false)
	private Long id;

	@Embedded private Auth auth;

	@Length(max = 10) @Column(nullable = false, length = 10)
	private String name;

	@Length(max = 20) @Column(nullable = false, length = 20)
	private String job;

	@Column(nullable = false)
	private String profileImageUrl;

	@Column(nullable = false)
	private Boolean deleted;

	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	private List<MemberRole> memberRole;

	@Builder
	public Member(
			Auth auth, String name, String job, String profileImageUrl, Boolean deleted, List<MemberRole> memberRole) {
		this.auth = auth;
		this.name = name;
		this.job = job;
		this.profileImageUrl = profileImageUrl;
		this.deleted = deleted;
		this.memberRole = memberRole;
	}

	public static Member createNoJobMember(Auth auth, String name, String profileImageUrl) {
		return Member.builder()
				.auth(auth)
				.name(name)
				.job("")
				.profileImageUrl(profileImageUrl)
				.deleted(false)
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
}
