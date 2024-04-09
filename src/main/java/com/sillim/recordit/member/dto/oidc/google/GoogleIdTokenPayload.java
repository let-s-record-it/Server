package com.sillim.recordit.member.dto.oidc.google;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleIdTokenPayload(
		String iss,
		String azp,
		String aud,
		String sub,
		String email,
		Boolean emailVerified,
		String name,
		String picture,
		String givenName,
		String iat,
		String exp) {}
