package com.sillim.recordit.member.dto.oidc.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfo(Long id, LocalDateTime connectedAt, KakaoAccount kakaoAccount) {
}
