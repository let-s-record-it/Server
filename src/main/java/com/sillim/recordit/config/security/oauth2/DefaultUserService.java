package com.sillim.recordit.config.security.oauth2;

import com.sillim.recordit.config.security.oauth2.mapper.AttributeMapperFactory;
import com.sillim.recordit.config.security.oauth2.mapper.LoginMemberMapper;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.member.service.SignupService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserService extends DefaultOAuth2UserService {

	private final AttributeMapperFactory attributeMapperFactory;
	private final LoginMemberMapper loginMemberMapper;
	private final SignupService signupService;
	private final MemberQueryService memberQueryService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuthProvider provider =
				OAuthProvider.valueOf(
						userRequest.getClientRegistration().getClientName().toUpperCase());
		OAuth2User oAuth2User = super.loadUser(userRequest);
		OAuth2Request oAuth2Request =
				attributeMapperFactory
						.getAttributeMapper(provider)
						.mapToDto(oAuth2User.getAttributes());

		Optional<Member> member = memberQueryService.searchByAccount(oAuth2Request.account());
		if (member.isPresent()) {
			return loginMemberMapper.toLoginMember(member.get());
		}

		return loginMemberMapper.toLoginMember(
				signupService.signup(
						new MemberInfo(
								oAuth2Request.account(),
								oAuth2Request.provider(),
								oAuth2Request.name(),
								oAuth2Request.imageUrl())));
	}
}
