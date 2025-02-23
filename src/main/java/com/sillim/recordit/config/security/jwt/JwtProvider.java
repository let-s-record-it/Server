package com.sillim.recordit.config.security.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	private final Key secretKey;

	private static final Long EXCHANGE_TOKEN_VALIDATION_SECOND = 60L * 1000;
	private static final Long ACCESS_TOKEN_VALIDATION_SECOND = 60L * 60 * 24 * 1000;
	private static final Long REFRESH_TOKEN_VALIDATION_SECOND = 60L * 60 * 24 * 14 * 1000;

	public String generateExchangeToken(Long memberId) {
		return buildToken(memberId)
				.setExpiration(
						Date.from(
								Instant.now()
										.plus(
												EXCHANGE_TOKEN_VALIDATION_SECOND,
												ChronoUnit.SECONDS)))
				.compact();
	}

	public AuthorizationToken generateAuthorizationToken(Long memberId) {
		return new AuthorizationToken(
				generateAccessToken(memberId), generateRefreshToken(memberId));
	}

	private String generateAccessToken(Long memberId) {
		return buildToken(memberId)
				.setExpiration(
						Date.from(
								Instant.now()
										.plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();
	}

	private String generateRefreshToken(Long memberId) {
		return buildToken(memberId)
				.setExpiration(
						Date.from(
								Instant.now()
										.plus(REFRESH_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();
	}

	private JwtBuilder buildToken(Long memberId) {
		return Jwts.builder()
				.setSubject(String.valueOf(memberId))
				.signWith(secretKey, SignatureAlgorithm.HS512);
	}
}
