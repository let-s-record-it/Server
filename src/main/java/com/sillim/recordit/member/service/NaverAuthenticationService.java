package com.sillim.recordit.member.service;

import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.domain.TokenType;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.oidc.naver.NaverUserInfo;
import com.sillim.recordit.member.dto.request.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverAuthenticationService implements AuthenticationService {

	private final NaverUserInfoClient naverUserInfoClient;

	@Override
	public String authenticate(IdToken idToken) {
		return null;
	}

	@Override
	public MemberInfo getMemberInfoByAccessToken(String accessToken) {
		NaverUserInfo naverUserInfo =
				naverUserInfoClient.getNaverUserInfo(
						TokenType.BEARER.getValueWithSpace() + accessToken);

		return MemberInfo.builder()
				.oauthAccount(naverUserInfo.response().id())
				.oAuthProvider(OAuthProvider.NAVER)
				.name(naverUserInfo.response().nickname())
				.build();
	}
}
