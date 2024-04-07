package com.sillim.recordit.member.dto.kakao;

import com.sillim.recordit.config.security.encrypt.RsaKeyUtils;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.member.InvalidIdTokenException;
import io.jsonwebtoken.Jwts;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public record KakaoIdTokenHeader(String kid, String typ, String alg) {

    public void validateSignature(String idToken, List<KakaoOidcPublicKey> keys) {
        KakaoOidcPublicKey publicKey = getPublicKey(keys);

        try {
            Jwts.parserBuilder()
                    .setSigningKey(RsaKeyUtils.getKeyByRsa(publicKey.n(), publicKey.e()))
                    .build()
                    .parseClaimsJws(idToken);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InvalidIdTokenException(ErrorCode.ID_TOKEN_INVALID_SIGNATURE);
        }
    }

    private KakaoOidcPublicKey getPublicKey(List<KakaoOidcPublicKey> keys) {
        return keys.stream()
                .filter(key -> key.kid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new InvalidIdTokenException(ErrorCode.ID_TOKEN_UNSUPPORTED));
    }
}
