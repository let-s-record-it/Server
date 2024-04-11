package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
class KakaoOidcClientTest {

	@Autowired CacheManager cacheManager;
	@Autowired KakaoOidcClient kakaoOidcClient;

	@BeforeEach
	void setUp() {
		kakaoOidcClient.getOidcPublicKeys();
	}

	@Test
	@DisplayName("Kakao PublicKeys 요청 시 캐싱된 것을 먼저 불러온다.")
	void findKakaoPublicKeysThatCached() {
		Optional<OidcPublicKeys> cachedPublicKeys =
				Optional.ofNullable(cacheManager.getCache("publicKeys"))
						.map(cache -> cache.get("kakao", OidcPublicKeys.class));

		OidcPublicKeys oidcPublicKeys = kakaoOidcClient.getOidcPublicKeys();

		assertThat(cachedPublicKeys).isNotEmpty();
		assertThat(oidcPublicKeys).isEqualTo(cachedPublicKeys.get());
	}
}
