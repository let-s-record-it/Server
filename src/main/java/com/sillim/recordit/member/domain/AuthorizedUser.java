package com.sillim.recordit.member.domain;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class AuthorizedUser implements OAuth2User {

	private final Member member;
	private final Map<String, Object> attributes;
	private final Collection<? extends GrantedAuthority> authorities;

	public AuthorizedUser(Member member, Map<String, Object> attributes,
			Collection<? extends GrantedAuthority> authorities) {
		this.member = member;
		this.attributes = attributes;
		this.authorities = authorities;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getName() {
		return member.getId().toString();
	}

	public Member getMember() {
		return member;
	}

	public Long getMemberId() {
		return member.getId();
	}
}
