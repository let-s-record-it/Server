package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.config.security.jwt.JwtProvider;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.request.LoginRequest;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.fixture.AuthorizationTokenFixture;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.repository.MemberRepository;
import com.sillim.recordit.member.service.kakao.KakaoAuthenticationService;
import com.sillim.recordit.member.service.naver.NaverAuthenticationService;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

	@Mock KakaoAuthenticationService kakaoAuthenticationService;
	@Mock NaverAuthenticationService naverAuthenticationService;
	@Mock JwtProvider jwtProvider;
	@Mock MemberRepository memberRepository;
	@Mock ObjectMapper objectMapper;
	@Mock SignupService signupService;
	@Mock MemberDeviceService memberDeviceService;
	@InjectMocks LoginService loginService;

	@Test
	@DisplayName("oidc를 지원하는 로그인의 경우 토큰을 검증하여 로그인 한다.")
	void login() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String idToken = "header.payload.signature";
		String account = "account";
		given(kakaoAuthenticationService.authenticate(any(IdToken.class))).willReturn(account);
		given(memberRepository.findByAuthOauthAccount(eq(account))).willReturn(Optional.of(member));
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		AuthorizationToken authorizationToken =
				loginService.login(
						new LoginRequest(
								idToken,
								"accessToken",
								OAuthProvider.KAKAO,
								"id",
								"model",
								"token"));

		assertThat(authorizationToken.accessToken()).isEqualTo(target.accessToken());
		assertThat(authorizationToken.refreshToken()).isEqualTo(target.refreshToken());
	}

	@Test
	@DisplayName("로그인 시 인가받은 유저 정보가 없을 경우 회원가입 후 로그인 한다.")
	void signInIfNotExistsMemberInfo() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String idToken = "header.payload.signature";
		String account = "account";
		MemberInfo memberInfo =
				new MemberInfo(account, OAuthProvider.KAKAO, "name", "https://image.url");
		given(kakaoAuthenticationService.authenticate(any(IdToken.class))).willReturn(account);
		given(memberRepository.findByAuthOauthAccount(eq(account))).willReturn(Optional.empty());
		given(kakaoAuthenticationService.getMemberInfoByAccessToken(anyString()))
				.willReturn(memberInfo);
		given(signupService.signup(eq(memberInfo))).willReturn(member);
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		AuthorizationToken authorizationToken =
				loginService.login(
						new LoginRequest(
								idToken,
								"accessToken",
								OAuthProvider.KAKAO,
								"id",
								"model",
								"token"));

		assertThat(authorizationToken.accessToken()).isEqualTo(target.accessToken());
		assertThat(authorizationToken.refreshToken()).isEqualTo(target.refreshToken());
	}

	@Test
	@DisplayName("네이버 로그인 시 엑세스 토큰으로 인가받아 로그인 한다.")
	void naverLoginWithoutOidcToken() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String account = "account";
		MemberInfo memberInfo =
				new MemberInfo(account, OAuthProvider.NAVER, "name", "https://image.url");
		given(naverAuthenticationService.getMemberInfoByAccessToken(anyString()))
				.willReturn(memberInfo);
		given(memberRepository.findByAuthOauthAccount(eq(account))).willReturn(Optional.of(member));
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		AuthorizationToken authorizationToken =
				loginService.login(
						new LoginRequest(
								"", "accessToken", OAuthProvider.NAVER, "id", "model", "token"));

		assertThat(authorizationToken.accessToken()).isEqualTo(target.accessToken());
		assertThat(authorizationToken.refreshToken()).isEqualTo(target.refreshToken());
	}

	@Test
	@DisplayName("네이버 로그인 시 인가받은 유저 정보가 없을 경우 회원가입 후 로그인 한다.")
	void naverSignInIfNotExistsMemberInfo() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String account = "account";
		MemberInfo memberInfo =
				new MemberInfo(account, OAuthProvider.NAVER, "name", "https://image.url");
		given(naverAuthenticationService.getMemberInfoByAccessToken(anyString()))
				.willReturn(memberInfo);
		given(memberRepository.findByAuthOauthAccount(eq(account))).willReturn(Optional.empty());
		given(signupService.signup(eq(memberInfo))).willReturn(member);
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		AuthorizationToken authorizationToken =
				loginService.login(
						new LoginRequest(
								"", "accessToken", OAuthProvider.NAVER, "id", "model", "token"));

		assertThat(authorizationToken.accessToken()).isEqualTo(target.accessToken());
		assertThat(authorizationToken.refreshToken()).isEqualTo(target.refreshToken());
	}
}
