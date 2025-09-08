package com.sillim.recordit.member.service.google;

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
class GoogleOidcClientTest {

	@Autowired
	CacheManager cacheManager;
	@Autowired
	GoogleOidcClient googleOidcClient;

	@Test
	@DisplayName("Google PublicKeys를 조회한다.")
	void findGooglePublicKeys() {
		OidcPublicKeys oidcPublicKeys = googleOidcClient.getOidcPublicKeys();

		assertThat(oidcPublicKeys.keys()).hasSizeGreaterThan(0);
	}

	@Test
	@DisplayName("Google PublicKeys 요청 시 캐싱된 것을 먼저 불러온다.")
	void findGooglePublicKeysThatCached() {
		googleOidcClient.getOidcPublicKeys();
		Optional<OidcPublicKeys> cachedPublicKeys = Optional.ofNullable(cacheManager.getCache("publicKeys"))
				.map(cache -> cache.get("google", OidcPublicKeys.class));

		OidcPublicKeys oidcPublicKeys = googleOidcClient.getOidcPublicKeys();

		assertThat(cachedPublicKeys).isNotEmpty();
		assertThat(oidcPublicKeys).isEqualTo(cachedPublicKeys.get());
	}
}
