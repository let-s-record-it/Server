package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.oidc.kakao.KakaoUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "KakaoUserInfoClient", url = "https://kapi.kakao.com")
@Component
public interface KakaoUserInfoClient {

    @GetMapping("/v2/user/me")
    KakaoUserInfo getKakaoUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization);
}
