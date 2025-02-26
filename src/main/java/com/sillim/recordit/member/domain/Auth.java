package com.sillim.recordit.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Auth {

	@Column private String oauthAccount;

	@Enumerated(EnumType.STRING)
	@Column
	private OAuthProvider oauthProvider;

	public Auth(String oauthAccount, OAuthProvider oauthProvider) {
		this.oauthAccount = oauthAccount;
		this.oauthProvider = oauthProvider;
	}
}
