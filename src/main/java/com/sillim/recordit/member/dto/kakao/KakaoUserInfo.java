package com.sillim.recordit.member.dto.response;

import java.time.LocalDateTime;

public record KakaoUserInfo(Long id,
                            LocalDateTime connectedAt,
                            KakaoAccount kakaoAccount) {

}
