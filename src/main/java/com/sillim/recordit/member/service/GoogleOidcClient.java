package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.oidc.OidcPublicKeys;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "GoogleOidcClient", url = "https://www.googleapis.com")
public interface GoogleOidcClient {

	@Cacheable(cacheNames = "publicKeys", key = "'google'")
	@GetMapping("/oauth2/v3/certs")
	OidcPublicKeys getOidcPublicKeys();
}
