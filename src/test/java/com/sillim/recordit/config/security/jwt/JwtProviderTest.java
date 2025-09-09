package com.sillim.recordit.config.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

import com.sillim.recordit.config.security.encrypt.AESEncryptor;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @Mock
    AESEncryptor aesEncryptor;
    @InjectMocks
    JwtProvider jwtProvider;

    @Test
    @DisplayName("인가 토큰을 생성한다.")
    void generateAuthorizationToken() throws Exception {
        String signature = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest"
                + "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest" + "testtest";
        String email = "test@mail.com";
        String secret = "secret";
        String encrypted = "encrypted";
        Key secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(signature.getBytes()));
        ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtProvider, "secret", secret);
        BDDMockito.given(aesEncryptor.encrypt(eq(email), eq(secret))).willReturn(encrypted);

        AuthorizationToken token = jwtProvider.generateAuthorizationToken(email);

        assertThat(Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token.accessToken()).getBody()
                .getSubject()).isEqualTo(encrypted);
        assertThat(Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token.refreshToken()).getBody()
                .getSubject()).isEqualTo(encrypted);
    }
}
