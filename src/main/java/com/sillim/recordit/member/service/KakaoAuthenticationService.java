package com.sillim.recordit.member.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.member.InvalidAccessTokenException;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.oidc.kakao.KakaoOidcClient;
import com.sillim.recordit.member.dto.oidc.kakao.KakaoUserInfo;
import com.sillim.recordit.member.dto.request.SignupRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthenticationService implements AuthenticationService {

	private static final String ISS = "https://kauth.kakao.com";
	private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

	private final RestTemplate restTemplate;
	private final KakaoOidcClient kakaoOidcClient;

	@Value("${app-key.kakao}")
	private String appKey;

	@Override
	@Transactional
	public String authenticate(IdToken idToken) {
		return idToken.authenticateToken(kakaoOidcClient.getOidcPublicKeys(), ISS, appKey);
	}

	@Override
	public SignupRequest getMemberInfoByAccessToken(String accessToken) {
		HttpHeaders authorizationHeader = setAuthorizationHeader(accessToken);
		URI uri = UriComponentsBuilder.fromUriString(KAKAO_USER_INFO_URL).encode().build().toUri();
		ResponseEntity<KakaoUserInfo> response =
				restTemplate.exchange(
						new RequestEntity<>(authorizationHeader, HttpMethod.GET, uri),
						KakaoUserInfo.class);

		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new InvalidAccessTokenException(ErrorCode.INVALID_KAKAO_TOKEN);
		}

		return SignupRequest.builder()
				.oauthAccount(response.getBody().id().toString())
				.oAuthProvider(OAuthProvider.KAKAO)
				.name(response.getBody().kakaoAccount().profile().nickname())
				.build();
	}

	private static HttpHeaders setAuthorizationHeader(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
		headers.set(
				HttpHeaders.CONTENT_TYPE,
				new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8)
						.toString());
		return headers;
	}
}