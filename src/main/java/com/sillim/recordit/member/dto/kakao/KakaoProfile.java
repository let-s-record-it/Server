package com.sillim.recordit.member.dto.response;

public record KakaoProfile(String nickname,
                           String thumbnailImageUrl,
                           String profileImageUrl,
                           Boolean isDefaultImage,
                           Boolean isDefaultNickname) {

}
