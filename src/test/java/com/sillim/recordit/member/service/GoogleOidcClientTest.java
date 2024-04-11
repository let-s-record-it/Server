package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.RecorditApplication;
import com.sillim.recordit.member.dto.oidc.OidcPublicKeys;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RecorditApplication.class)
class GoogleOidcClientTest {

	@Autowired CacheManager cacheManager;
	@Autowired GoogleOidcClient googleOidcClient;

	@BeforeEach
	void setUp() {
		googleOidcClient.getOidcPublicKeys();
	}

	@Test
	@DisplayName("Google PublicKeys 요청 시 캐싱된 것을 먼저 불러온다.")
	void findKakaoPublicKeysThatCached() {
		Optional<OidcPublicKeys> cachedPublicKeys =
				Optional.ofNullable(cacheManager.getCache("publicKeys"))
						.map(cache -> cache.get("google", OidcPublicKeys.class));

		OidcPublicKeys oidcPublicKeys = googleOidcClient.getOidcPublicKeys();

		assertThat(cachedPublicKeys).isNotEmpty();
		assertThat(oidcPublicKeys).isEqualTo(cachedPublicKeys.get());
	}
}
