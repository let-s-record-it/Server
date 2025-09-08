package com.sillim.recordit.member.dto.oidc;

import com.sillim.recordit.config.security.encrypt.RsaKeyUtils;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.member.InvalidIdTokenException;
import io.jsonwebtoken.Jwts;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public record IdTokenHeader(String kid, String typ, String alg) {

	public void validateSignature(String idToken, OidcPublicKeys publicKeys) {
		OidcPublicKey publicKey = publicKeys.getPublicKey(this.kid);

		try {
			Jwts.parserBuilder().setSigningKey(RsaKeyUtils.getKeyByRsa(publicKey.n(), publicKey.e())).build()
					.parseClaimsJws(idToken);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new InvalidIdTokenException(ErrorCode.ID_TOKEN_INVALID_SIGNATURE);
		}
	}
}
