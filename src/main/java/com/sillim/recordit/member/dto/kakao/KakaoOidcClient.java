package com.sillim.recordit.member.dto.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "KakaoOidcClient", url = "https://kauth.kakao.com")
@Component
public interface KakaoOidcClient {

	@GetMapping("/.well-known/jwks.json")
	KakaoOidePublicKeys getKakaoOidcPublicKeys();
}
