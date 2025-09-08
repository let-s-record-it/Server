package com.sillim.recordit.member.service.google;

import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.domain.TokenType;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.oidc.google.GoogleUserInfo;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthenticationService implements AuthenticationService {

	private static final String ISS = "https://accounts.google.com";

	@Value("${app-key.google}")
	private String appKey;

	private final GoogleOidcClient googleOidcClient;
	private final GoogleUserInfoClient googleUserInfoClient;

	@Override
	public String authenticate(IdToken idToken) {
		return idToken.authenticateToken(googleOidcClient.getOidcPublicKeys(), ISS, appKey);
	}

	@Override
	public MemberInfo getMemberInfoByAccessToken(String accessToken) {
		GoogleUserInfo googleUserInfo = googleUserInfoClient
				.getGoogleUserInfo(TokenType.BEARER.getValueWithSpace() + accessToken);

		return MemberInfo.builder().oauthAccount(googleUserInfo.sub()).oAuthProvider(OAuthProvider.GOOGLE)
				.name(googleUserInfo.name()).email(googleUserInfo.email()).profileImageUrl(googleUserInfo.picture())
				.build();
	}
}
