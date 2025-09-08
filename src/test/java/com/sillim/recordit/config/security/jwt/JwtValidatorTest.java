package com.sillim.recordit.config.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.security.InvalidJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtValidatorTest {

	private static final Long ACCESS_TOKEN_VALIDATION_SECOND = 60L * 60 * 24 * 1000;
	String signature = "signaturesignaturesignaturesignaturesignaturesignature";
	SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(signature));
	String memberId = "1";
	JwtValidator jwtValidator = new JwtValidator(secretKey);

	@Test
	@DisplayName("토큰이 만료되면 InvalidJwtException 이 발생한다.")
	void throwInvalidJwtExceptionIfTokenIsExpired() {
		String token = Jwts.builder().setSubject(memberId).signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().minus(1L, ChronoUnit.SECONDS))).compact();

		assertThatThrownBy(() -> jwtValidator.getMemberIdIfValid(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage(ErrorCode.JWT_EXPIRED.getDescription());
	}

	@Test
	@DisplayName("JWT가 손상되면 InvalidJwtException 이 발생한다.")
	void throwMalformedJwtExceptionIfTokenIsMalformed() {
		String token = Jwts.builder().setSubject(memberId).signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact().substring(1);

		assertThatThrownBy(() -> jwtValidator.validateToken(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage(ErrorCode.JWT_MALFORMED.getDescription());
	}

	@Test
	@DisplayName("Signauture가 맞지 않으면 InvalidJwtException 이 발생한다.")
	void throwInvalidJwtExceptionIfSignatureIsInvalid() {
		String signature = "1signaturesignaturesignaturesignaturesignaturesignature";
		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(signature));
		String token = Jwts.builder().setSubject(memberId).signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();

		assertThatThrownBy(() -> jwtValidator.validateToken(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage(ErrorCode.JWT_INVALID_SIGNATURE.getDescription());
	}

	@Test
	@DisplayName("지원하지 않는 JWT 형태이면 InvalidJwtException 이 발생한다.")
	void throwInvalidJwtExceptionIfJwtIsUnsupported() {
		String token = Jwts.builder().setSubject(memberId)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();

		assertThatThrownBy(() -> jwtValidator.validateToken(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage(ErrorCode.JWT_UNSUPPORTED.getDescription());
	}

	@Test
	@DisplayName("JWT 검증에 성공하면 멤버 ID를 반환한다.")
	void getMemberIdIfJwtIsInvalid() {
		String token = Jwts.builder().setSubject(memberId).signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();

		Long memberId = jwtValidator.getMemberIdIfValid(token);

		assertThat(memberId).isEqualTo(Long.parseLong(this.memberId));
	}

	@Test
	@DisplayName("JWT 검증에 성공해도 멤버 ID가 JWT에 들어있지 않으면 InvalidJwtException 이 발생한다.")
	void throwInvalidJwtExceptionIfNotExistsMemberIdInJwt() {
		String token = Jwts.builder().signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();

		assertThatThrownBy(() -> jwtValidator.getMemberIdIfValid(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage("유저 ID를 찾을 수 없습니다.");
	}
}
