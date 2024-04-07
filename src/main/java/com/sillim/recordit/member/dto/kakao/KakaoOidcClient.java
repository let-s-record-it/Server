package com.sillim.recordit.member.dto.response;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "KakaoOidcClient", url = "https://kauth.kakao.com")
public interface KakaoOidcClient {

    @GetMapping("/.well-known/jwks.json")
    List<KakaoOidcPublicKey> getKakaoOidcPublicKeys();
}
