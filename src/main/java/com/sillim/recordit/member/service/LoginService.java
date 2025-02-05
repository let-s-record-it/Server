package com.sillim.recordit.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.config.security.jwt.JwtProvider;
import com.sillim.recordit.config.security.jwt.JwtValidator;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.oidc.IdTokenHeader;
import com.sillim.recordit.member.dto.oidc.IdTokenPayload;
import com.sillim.recordit.member.dto.request.LoginRequest;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.repository.MemberRepository;
import com.sillim.recordit.member.service.google.GoogleAuthenticationService;
import com.sillim.recordit.member.service.kakao.KakaoAuthenticationService;
import com.sillim.recordit.member.service.naver.NaverAuthenticationService;
import java.io.IOException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

	private final Map<OAuthProvider, AuthenticationService> authenticationServiceMap =
			new EnumMap<>(OAuthProvider.class);
	private final JwtProvider jwtProvider;
	private final JwtValidator jwtValidator;
	private final ObjectMapper objectMapper;
	private final MemberRepository memberRepository;
	private final SignupService signupService;
	private final MemberDeviceService memberDeviceService;

	public LoginService(
			JwtProvider jwtProvider,
			JwtValidator jwtValidator,
			ObjectMapper objectMapper,
			MemberRepository memberRepository,
			SignupService signupService,
			MemberDeviceService memberDeviceService,
			KakaoAuthenticationService kakaoAuthenticationService,
			GoogleAuthenticationService googleAuthenticationService,
			NaverAuthenticationService naverAuthenticationService) {
		this.jwtProvider = jwtProvider;
		this.jwtValidator = jwtValidator;
		this.objectMapper = objectMapper;
		this.memberRepository = memberRepository;
		this.signupService = signupService;
		this.memberDeviceService = memberDeviceService;
		authenticationServiceMap.put(OAuthProvider.KAKAO, kakaoAuthenticationService);
		authenticationServiceMap.put(OAuthProvider.GOOGLE, googleAuthenticationService);
		authenticationServiceMap.put(OAuthProvider.NAVER, naverAuthenticationService);
	}

	public AuthorizationToken login(String exchangeToken) {
        return jwtProvider.generateAuthorizationToken(jwtValidator.getMemberIdIfValid(exchangeToken));
	}

	@Transactional
	public AuthorizationToken login(LoginRequest loginRequest) throws IOException {
		AuthenticationService authenticationService =
				authenticationServiceMap.get(loginRequest.provider());

		if (loginRequest.provider().equals(OAuthProvider.NAVER)) {
			return loginWithoutOidc(loginRequest, authenticationService);
		}

		Member member =
				memberRepository
						.findByAuthOauthAccount(
								authenticationService.authenticate(
										parseToken(loginRequest.idToken())))
						.orElseGet(
								() ->
										signupService.signup(
												authenticationService.getMemberInfoByAccessToken(
														loginRequest.accessToken())));
		memberDeviceService.addMemberDeviceIfNotExists(
				loginRequest.deviceId(), loginRequest.model(), loginRequest.fcmToken(), member);
		return jwtProvider.generateAuthorizationToken(member.getId());
	}

	private AuthorizationToken loginWithoutOidc(
			LoginRequest loginRequest, AuthenticationService authenticationService) {
		MemberInfo memberInfo =
				authenticationService.getMemberInfoByAccessToken(loginRequest.accessToken());
		return jwtProvider.generateAuthorizationToken(
				memberRepository
						.findByAuthOauthAccount(memberInfo.oauthAccount())
						.orElseGet(() -> signupService.signup(memberInfo))
						.getId());
	}

	private IdToken parseToken(String idToken) throws IOException {
		String[] tokenParts = idToken.split("\\.");
		return new IdToken(
				objectMapper.readValue(
						Base64.getDecoder().decode(tokenParts[0]), IdTokenHeader.class),
				objectMapper.readValue(
						Base64.getDecoder().decode(tokenParts[1]), IdTokenPayload.class),
				idToken);
	}
}
