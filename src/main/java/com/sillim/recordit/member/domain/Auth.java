package com.sillim.recordit.member.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Auth {

	private String oauthAccount;
	private OAuthProvider oauthProvider;

	public Auth(String oauthAccount, OAuthProvider oauthProvider) {
		this.oauthAccount = oauthAccount;
		this.oauthProvider = oauthProvider;
	}
}
