package com.sillim.recordit.member.service.kakao;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.member.dto.oidc.OidcPublicKeys;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class KakaoOidcClientTest {

	@Autowired CacheManager cacheManager;
	@Autowired KakaoOidcClient kakaoOidcClient;

	@Test
	@DisplayName("Kakao PublicKeys를 조회한다.")
	void findKakaoPublicKeys() {
		OidcPublicKeys oidcPublicKeys = kakaoOidcClient.getOidcPublicKeys();

		assertThat(oidcPublicKeys.keys()).hasSizeGreaterThan(0);
	}

	@Test
	@DisplayName("Kakao PublicKeys 요청 시 캐싱된 것을 먼저 불러온다.")
	void findKakaoPublicKeysThatCached() {
		kakaoOidcClient.getOidcPublicKeys();
		Optional<OidcPublicKeys> cachedPublicKeys =
				Optional.ofNullable(cacheManager.getCache("publicKeys"))
						.map(cache -> cache.get("kakao", OidcPublicKeys.class));

		OidcPublicKeys oidcPublicKeys = kakaoOidcClient.getOidcPublicKeys();

		assertThat(cachedPublicKeys).isNotEmpty();
		assertThat(oidcPublicKeys).isEqualTo(cachedPublicKeys.get());
	}
}
