package com.sillim.recordit.config.security.jwt;

import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Bean
	public Key key() {
		return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey.getBytes()));
	}
}
