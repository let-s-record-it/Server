package com.sillim.recordit.member.dto.response;

public record KakaoAccount(Boolean profileNeedsAgreement,
                           Boolean profileNicknameNeedsAgreement,
                           Boolean profileImageNeedsAgreement,
                           KakaoProfile profile) {

}
