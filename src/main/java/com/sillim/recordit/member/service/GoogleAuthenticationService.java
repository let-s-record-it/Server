package com.sillim.recordit.member.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.member.InvalidAccessTokenException;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.oidc.google.GoogleOidcClient;
import com.sillim.recordit.member.dto.oidc.google.GoogleUserInfo;
import com.sillim.recordit.member.dto.request.SignupRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthenticationService implements AuthenticationService {

	private static final String ISS = "https://accounts.google.com";
	public static final String GOOGLE_USER_INFO_URL =
			"https://openidconnect.googleapis.com/v1/userinfo";

	private final RestTemplate restTemplate;
	private final GoogleOidcClient googleOidcClient;

	@Value("${app-key.google}")
	private String appKey;

	@Override
	public String authenticate(IdToken idToken) {
		return idToken.authenticateToken(googleOidcClient.getOidcPublicKeys(), ISS, appKey);
	}

	@Override
	public SignupRequest getMemberInfoByAccessToken(String accessToken) {
		HttpHeaders authorizationHeader = setAuthorizationHeader(accessToken);
		URI uri = UriComponentsBuilder.fromUriString(GOOGLE_USER_INFO_URL).encode().build().toUri();
		ResponseEntity<GoogleUserInfo> response =
				restTemplate.exchange(
						new RequestEntity<>(authorizationHeader, HttpMethod.GET, uri),
						GoogleUserInfo.class);

		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new InvalidAccessTokenException(ErrorCode.INVALID_KAKAO_TOKEN);
		}

		return SignupRequest.builder()
				.oauthAccount(response.getBody().sub())
				.oAuthProvider(OAuthProvider.GOOGLE)
				.name(response.getBody().name())
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
