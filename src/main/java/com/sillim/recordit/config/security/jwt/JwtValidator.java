package com.sillim.recordit.config.security.jwt;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.security.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtValidator {

	private final Key secretKey;

	public Long getMemberIdIfValid(String accessToken) {
		String memberId = validateToken(accessToken).getBody().getSubject();

		if (Objects.isNull(memberId)) {
			throw new InvalidJwtException(ErrorCode.JWT_UNSUPPORTED, "유저 ID를 찾을 수 없습니다.");
		}
		return Long.valueOf(memberId);
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
