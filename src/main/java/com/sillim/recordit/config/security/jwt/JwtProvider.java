package com.sillim.recordit.config.security.jwt;

import com.sillim.recordit.config.security.encrypt.AESEncryptor;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	@Value("${jwt.secret-key}")
	private String secret;

	private static final Long EXCHANGE_TOKEN_VALIDATION_SECOND = 60L * 1000;
	private static final Long ACCESS_TOKEN_VALIDATION_SECOND = 60L * 60 * 24 * 1000;
	private static final Long REFRESH_TOKEN_VALIDATION_SECOND = 60L * 60 * 24 * 14 * 1000;

	private final Key secretKey;
	private final AESEncryptor encryptor;

	public String generateExchangeToken(String email)
			throws NoSuchPaddingException,
					IllegalBlockSizeException,
					NoSuchAlgorithmException,
					BadPaddingException,
					InvalidKeyException {
		return buildToken(email)
				.setExpiration(
						Date.from(
								Instant.now()
										.plus(EXCHANGE_TOKEN_VALIDATION_SECOND, ChronoUnit.MILLIS)))
				.compact();
	}

	public AuthorizationToken generateAuthorizationToken(String email) throws Exception {
		return new AuthorizationToken(generateAccessToken(email), generateRefreshToken(email));
	}

	private String generateAccessToken(String email)
			throws NoSuchPaddingException,
					IllegalBlockSizeException,
					NoSuchAlgorithmException,
					BadPaddingException,
					InvalidKeyException {
		return buildToken(email)
				.setExpiration(
						Date.from(
								Instant.now()
										.plus(ACCESS_TOKEN_VALIDATION_SECOND, ChronoUnit.MILLIS)))
				.compact();
	}

	private String generateRefreshToken(String email)
			throws NoSuchPaddingException,
					IllegalBlockSizeException,
					NoSuchAlgorithmException,
					BadPaddingException,
					InvalidKeyException {
		return buildToken(email)
				.setExpiration(
						Date.from(
								Instant.now()
										.plus(REFRESH_TOKEN_VALIDATION_SECOND, ChronoUnit.MILLIS)))
				.compact();
	}

	private JwtBuilder buildToken(String email)
			throws NoSuchPaddingException,
					IllegalBlockSizeException,
					NoSuchAlgorithmException,
					BadPaddingException,
					InvalidKeyException {
		return Jwts.builder()
				.setSubject(encryptor.encrypt(email, secret))
				.signWith(secretKey, SignatureAlgorithm.HS512);
	}
}
