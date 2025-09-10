package com.sillim.recordit.member.dto.oidc.naver;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NaverProfile(
		String id, String nickname, String profileImage, String email, String name) {}
