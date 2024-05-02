package com.sillim.recordit.member.fixture;

import com.sillim.recordit.member.dto.oidc.OidcPublicKey;

public enum KakaoOidcPublicKeyFixture {
	DEFAULT("9f252dadd5f233f93d2fa528d12fea", "RS256", "", "", "", "");

	private final String kid;
	private final String alg;
	private final String kty;
	private final String use;
	private final String n;
	private final String e;

	KakaoOidcPublicKeyFixture(String kid, String alg, String kty, String use, String n, String e) {
		this.kid = kid;
		this.alg = alg;
		this.kty = kty;
		this.use = use;
		this.n = n;
		this.e = e;
	}

	public OidcPublicKey getKey() {
		return new OidcPublicKey(this.kid, this.alg, this.kty, this.use, this.n, this.e);
	}
}
