package com.sillim.recordit.member.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoProfile(String nickname,
						   String thumbnailImageUrl,
						   String profileImageUrl,
						   Boolean isDefaultImage,
						   Boolean isDefaultNickname) {}
