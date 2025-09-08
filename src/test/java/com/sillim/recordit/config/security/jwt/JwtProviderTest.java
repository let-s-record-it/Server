package com.sillim.recordit.config.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

	@InjectMocks
	JwtProvider jwtProvider;

	@Test
	@DisplayName("멤버 ID를 통해 인가 토큰을 생성한다.")
	void generateAuthorizationTokenByMemberId() {
		long memberId = 1L;
		String signature = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
				+ "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest" + "testtest";
		Key secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(signature.getBytes()));
		ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);

		AuthorizationToken token = jwtProvider.generateAuthorizationToken(memberId);

		assertThat(Long.parseLong(Jwts.parserBuilder().setSigningKey(secretKey).build()
				.parseClaimsJws(token.accessToken()).getBody().getSubject())).isEqualTo(memberId);
		assertThat(Long.parseLong(Jwts.parserBuilder().setSigningKey(secretKey).build()
				.parseClaimsJws(token.refreshToken()).getBody().getSubject())).isEqualTo(memberId);
	}
}
