package com.sillim.recordit.member.dto.oidc;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.member.InvalidIdTokenException;
import java.util.List;

public record OidcPublicKeys(List<OidcPublicKey> keys) {

	public OidcPublicKey getPublicKey(String kid) {
		return this.keys.stream().filter(key -> key.kid().equals(kid)).findFirst()
				.orElseThrow(() -> new InvalidIdTokenException(ErrorCode.ID_TOKEN_UNSUPPORTED));
	}
}
