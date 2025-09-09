package com.sillim.recordit.config.security.jwt;

import com.sillim.recordit.config.security.encrypt.AESEncryptor;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.security.InvalidJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtValidatorTest {

	private static final Long ACCESS_TOKEN_VALIDATION_SECOND = 60L * 60 * 24 * 1000;
	private static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";
	String signature = "signaturesignaturesignaturesignaturesignaturesignature";
	SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(signature));
	String email = "test@mail.com";
	AESEncryptor aesEncryptor = new AESEncryptor();
	JwtValidator jwtValidator = new JwtValidator(secretKey, aesEncryptor);

	@Test
	@DisplayName("토큰이 만료되면 InvalidJwtException 이 발생한다.")
	void throwInvalidJwtExceptionIfTokenIsExpired() throws Exception {
		String encrypted = aesEncryptor.encrypt(email, "secret");
		String token = Jwts.builder().setSubject(encrypted).signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().minus(1L, ChronoUnit.SECONDS))).compact();

		assertThatThrownBy(() -> jwtValidator.getSubIfValid(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage(ErrorCode.JWT_EXPIRED.getDescription());
	}

	@Test
	@DisplayName("JWT가 손상되면 InvalidJwtException 이 발생한다.")
	void throwMalformedJwtExceptionIfTokenIsMalformed() throws Exception {
		String encrypted = aesEncryptor.encrypt(email, "secret");
		String token = Jwts.builder().setSubject(encrypted).signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact().substring(1);

		assertThatThrownBy(() -> jwtValidator.validateToken(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage(ErrorCode.JWT_MALFORMED.getDescription());
	}

	@Test
	@DisplayName("Signauture가 맞지 않으면 InvalidJwtException 이 발생한다.")
	void throwInvalidJwtExceptionIfSignatureIsInvalid() throws Exception {
		String signature = "1signaturesignaturesignaturesignaturesignaturesignature";
		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(signature));
		String encrypted = aesEncryptor.encrypt(email, "secret");
		String token = Jwts.builder().setSubject(encrypted).signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();

		assertThatThrownBy(() -> jwtValidator.validateToken(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage(ErrorCode.JWT_INVALID_SIGNATURE.getDescription());
	}

	@Test
	@DisplayName("지원하지 않는 JWT 형태이면 InvalidJwtException 이 발생한다.")
	void throwInvalidJwtExceptionIfJwtIsUnsupported() throws Exception {
		String encrypted = aesEncryptor.encrypt(email, "secret");
		String token = Jwts.builder().setSubject(encrypted)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();

		assertThatThrownBy(() -> jwtValidator.validateToken(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage(ErrorCode.JWT_UNSUPPORTED.getDescription());
	}

	@Test
	@DisplayName("JWT 검증에 성공하면 subject를 반환한다.")
	void getMemberIdIfJwtIsInvalid() throws Exception {
		String encrypted = aesEncryptor.encrypt(email, "secret");
		ReflectionTestUtils.setField(jwtValidator, "secret", "secret");
		String token = Jwts.builder().setSubject(encrypted).signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();

		String sub = jwtValidator.getSubIfValid(token);

		assertThat(sub).isEqualTo(this.email);
	}

	@Test
	@DisplayName("JWT 검증에 성공해도 멤버 ID가 JWT에 들어있지 않으면 InvalidJwtException 이 발생한다.")
	void throwInvalidJwtExceptionIfNotExistsMemberIdInJwt() {
		String token = Jwts.builder().signWith(secretKey, SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.SECONDS)))
				.compact();

		assertThatThrownBy(() -> jwtValidator.getSubIfValid(token)).isInstanceOf(InvalidJwtException.class)
				.hasMessage("Subject를 찾을 수 없습니다.");
	}
}
