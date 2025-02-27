package com.sillim.recordit.config.security.oauth2.mapper;

import com.sillim.recordit.config.security.oauth2.OAuth2Request;
import com.sillim.recordit.member.domain.OAuthProvider;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KakaoAttributeMapper implements AttributeMapper {

	@Override
	public OAuth2Request mapToDto(Map<String, Object> attributes) {
		String account = attributes.get("id").toString();
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
		String name = (String) profile.getOrDefault("nickname", "");
		String imageUrl = (String) profile.getOrDefault("profile_image_url", "");
		String email = (String) kakaoAccount.get("email");
		return new OAuth2Request(account, OAuthProvider.KAKAO, name, imageUrl, email);
	}
}
