package com.sillim.recordit.config.security.jwt;

import com.sillim.recordit.config.security.encrypt.AESEncryptor;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.security.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtValidator {

	@Value("${jwt.secret-key}")
	private String secret;

	private final Key secretKey;
	private final AESEncryptor encryptor;

	public String getSubIfValid(String accessToken) throws NoSuchPaddingException, IllegalBlockSizeException,
			NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		String sub = validateToken(accessToken).getBody().getSubject();

		if (Objects.isNull(sub)) {
			throw new InvalidJwtException(ErrorCode.JWT_UNSUPPORTED, "Subject를 찾을 수 없습니다.");
		}
		return encryptor.decrypt(sub, secret);
	}

	public Jws<Claims> validateToken(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
		} catch (MalformedJwtException e) {
			throw new InvalidJwtException(ErrorCode.JWT_MALFORMED);
		} catch (UnsupportedJwtException e) {
			throw new InvalidJwtException(ErrorCode.JWT_UNSUPPORTED);
		} catch (ExpiredJwtException e) {
			throw new InvalidJwtException(ErrorCode.JWT_EXPIRED);
		} catch (SignatureException e) {
			throw new InvalidJwtException(ErrorCode.JWT_INVALID_SIGNATURE);
		}
	}
}
