package com.sillim.recordit.member.fixture;

import com.sillim.recordit.config.security.jwt.AuthorizationToken;

public enum AuthorizationTokenFixture {
	DEFAULT("access", "refresh");

	private final String accessToken;
	private final String refreshToken;

	AuthorizationTokenFixture(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public AuthorizationToken getAuthorizationToken() {
		return new AuthorizationToken(this.accessToken, this.refreshToken);
	}
}
