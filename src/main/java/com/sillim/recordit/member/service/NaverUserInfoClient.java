package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.oidc.naver.NaverUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "NaverUserInfoClient", url = "https://openapi.naver.com")
@Component
public interface NaverUserInfoClient {

	@GetMapping("/v1/nid/me")
	NaverUserInfo getNaverUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization);
}
