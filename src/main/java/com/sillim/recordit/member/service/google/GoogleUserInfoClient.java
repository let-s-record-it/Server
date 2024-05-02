package com.sillim.recordit.member.service.google;

import com.sillim.recordit.member.dto.oidc.google.GoogleUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "GoogleUserInfoClient", url = "https://openidconnect.googleapis.com")
public interface GoogleUserInfoClient {

	@GetMapping("/v1/userinfo")
	GoogleUserInfo getGoogleUserInfo(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization);
}
