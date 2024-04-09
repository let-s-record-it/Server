package com.sillim.recordit.member.dto.oidc;

public record IdToken(IdTokenHeader header, IdTokenPayload payload, String idToken) {

	public String authenticateToken(OidcPublicKeys publicKeys, String iss, String appKey) {
		this.header.validateSignature(idToken, publicKeys);
		this.payload.validatePayload(iss, appKey);
		return payload.sub();
	}
}
