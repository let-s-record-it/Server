package com.sillim.recordit.config.security.oauth2.mapper;

import com.sillim.recordit.config.security.oauth2.OAuth2Request;
import com.sillim.recordit.member.domain.OAuthProvider;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GoogleAttributeMapper implements AttributeMapper {

	@Override
	public OAuth2Request mapToDto(Map<String, Object> attributes) {
		String accountId = (String) attributes.get("sub");
		String name = (String) attributes.get("name");
		String email = (String) attributes.get("email");
		String imageUrl = (String) attributes.get("picture");
		return new OAuth2Request(accountId, OAuthProvider.GOOGLE, name, imageUrl, email);
	}
}
