package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.oidc.OidcPublicKeys;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "KakaoOidcClient", url = "https://kauth.kakao.com")
@Component
public interface KakaoOidcClient {

	@Cacheable(cacheNames = "publicKeys", key = "'kakao'")
	@GetMapping("/.well-known/jwks.json")
	OidcPublicKeys getOidcPublicKeys();
}