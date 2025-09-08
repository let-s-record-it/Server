package com.sillim.recordit.member.service.kakao;

import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.domain.TokenType;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.oidc.kakao.KakaoUserInfo;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthenticationService implements AuthenticationService {

	private static final String ISS = "https://kauth.kakao.com";

	@Value("${app-key.kakao}")
	private String appKey;

	private final KakaoOidcClient kakaoOidcClient;
	private final KakaoUserInfoClient kakaoUserInfoClient;

	@Override
	@Transactional
	public String authenticate(IdToken idToken) {
		return idToken.authenticateToken(kakaoOidcClient.getOidcPublicKeys(), ISS, appKey);
	}

	@Override
	public MemberInfo getMemberInfoByAccessToken(String accessToken) {
		KakaoUserInfo kakaoUserInfo = kakaoUserInfoClient
				.getKakaoUserInfo(TokenType.BEARER.getValueWithSpace() + accessToken);

		return MemberInfo.builder().oauthAccount(kakaoUserInfo.id().toString()).oAuthProvider(OAuthProvider.KAKAO)
				.name(kakaoUserInfo.kakaoAccount().profile().nickname()).email(kakaoUserInfo.kakaoAccount().email())
				.profileImageUrl(kakaoUserInfo.kakaoAccount().profile().profileImageUrl()).build();
	}
}
