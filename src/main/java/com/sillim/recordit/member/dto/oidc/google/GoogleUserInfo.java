package com.sillim.recordit.member.dto.oidc.google;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleUserInfo(
		String sub,
		String name,
		String givenName,
		String picture,
		String email,
		Boolean emailVerified,
		String locale) {}
