package com.sillim.recordit.member.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.member.InvalidAccessTokenException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.kakao.KakaoUserInfo;
import com.sillim.recordit.member.dto.request.SignupRequest;
import com.sillim.recordit.member.repository.MemberRepository;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class SignupService {

	public static final String BEARER = "Bearer ";
	public static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

	private final MemberRepository memberRepository;
	private final RestTemplate restTemplate;

	@Transactional
	public Member signup(SignupRequest signupRequest) {
		return memberRepository.save(signupRequest.toMember());
	}

	@Transactional
	public Member signupIfNotExistsByAccount(String account, String accessToken) {
		return memberRepository.findByAuthOauthAccount(account)
				.orElseGet(() -> signupByAccessToken(accessToken));
	}

	private Member signupByAccessToken(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
		headers.set(
				HttpHeaders.CONTENT_TYPE,
				new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8)
						.toString());
		URI uri = UriComponentsBuilder.fromUriString(KAKAO_USER_INFO_URL).encode().build().toUri();

		ResponseEntity<KakaoUserInfo> response =
				restTemplate.exchange(
						new RequestEntity<>(headers, HttpMethod.GET, uri), KakaoUserInfo.class);

		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new InvalidAccessTokenException(ErrorCode.INVALID_KAKAO_TOKEN);
		}

		return memberRepository.save(SignupRequest.builder()
				.oauthAccount(response.getBody().id().toString())
				.oAuthProvider(OAuthProvider.KAKAO)
				.name(response.getBody().kakaoAccount().profile().nickname())
				.build().toMember());
	}
}
