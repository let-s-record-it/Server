package com.sillim.recordit.config.security.oauth2.mapper;

import com.sillim.recordit.config.security.oauth2.OAuth2Request;
import com.sillim.recordit.member.domain.OAuthProvider;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NaverAttributeMapper implements AttributeMapper {

	@Override
	public OAuth2Request mapToDto(Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		String account = (String) response.get("id");
		String name = (String) response.get("name");
		String email = (String) response.get("email");
		String imageUrl = (String) response.getOrDefault("profile_image", "");

		return new OAuth2Request(account, OAuthProvider.NAVER, name, imageUrl, email);
	}
}
