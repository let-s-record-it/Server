package com.sillim.recordit.member.dto.oidc.google;

import com.sillim.recordit.member.dto.oidc.OidcClient;
import com.sillim.recordit.member.dto.oidc.OidcPublicKeys;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "GoogleOidcClient", url = "https://www.googleapis.com")
@Component
public interface GoogleOidcClient extends OidcClient {

	@Override
	@GetMapping("/oauth2/v3/certs")
	OidcPublicKeys getOidcPublicKeys();
}
