package com.sillim.recordit.config.security.oauth2.mapper;

import com.sillim.recordit.member.domain.OAuthProvider;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AttributeMapperFactory {

	private final Map<OAuthProvider, AttributeMapper> mapperMap =
			new EnumMap<>(OAuthProvider.class);

	public AttributeMapperFactory(KakaoAttributeMapper kakaoAttributeMapper,
								  NaverAttributeMapper naverAttributeMapper,
								  GoogleAttributeMapper googleAttributeMapper) {
		mapperMap.put(OAuthProvider.KAKAO, kakaoAttributeMapper);
		mapperMap.put(OAuthProvider.NAVER, naverAttributeMapper);
		mapperMap.put(OAuthProvider.GOOGLE, googleAttributeMapper);
	}

	public AttributeMapper getAttributeMapper(OAuthProvider oAuthProvider) {
		return mapperMap.get(oAuthProvider);
	}
}
