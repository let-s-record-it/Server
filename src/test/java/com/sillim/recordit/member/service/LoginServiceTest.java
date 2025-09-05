package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.config.security.jwt.JwtProvider;
import com.sillim.recordit.config.security.jwt.JwtValidator;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.member.InvalidRejoinException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.request.LoginRequest;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.dto.response.OAuthTokenResponse;
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
	@Mock MemberDeleteService memberDeleteService;
	@Mock JwtValidator jwtValidator;
	@InjectMocks LoginService loginService;

	@Test
	@DisplayName("oidc를 지원하는 로그인의 경우 토큰을 검증하여 로그인 한다.")
	void login() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String idToken = "header.payload.signature";
		String account = "account";
		given(kakaoAuthenticationService.authenticate(any(IdToken.class))).willReturn(account);
		given(memberRepository.findByOauthAccount(eq(account))).willReturn(Optional.of(member));
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		OAuthTokenResponse token =
				loginService.login(
						new LoginRequest(
								idToken,
								"accessToken",
								OAuthProvider.KAKAO,
								"id",
								"model",
								"token"));

		assertThat(token.accessToken()).isEqualTo(target.accessToken());
		assertThat(token.refreshToken()).isEqualTo(target.refreshToken());
	}

	@Test
	@DisplayName("웹 로그인의 경우 발급받은 토큰을 검증하여 로그인 한다.")
	void loginByToken() {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String token = "header.payload.signature";
		long memberId = 1L;
		given(jwtValidator.getMemberIdIfValid(eq(token))).willReturn(memberId);
		given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));
		given(jwtProvider.generateAuthorizationToken(eq(memberId))).willReturn(target);

		OAuthTokenResponse tokenResponse = loginService.login(token);

		assertThat(tokenResponse.accessToken()).isEqualTo(target.accessToken());
		assertThat(tokenResponse.refreshToken()).isEqualTo(target.refreshToken());
	}

	@Test
	@DisplayName("로그인 시 인가받은 유저 정보가 없을 경우 회원가입 후 로그인 한다.")
	void signInIfNotExistsMemberInfo() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String idToken = "header.payload.signature";
		String account = "account";
		MemberInfo memberInfo =
				new MemberInfo(
						account, OAuthProvider.KAKAO, "name", "test@mail.com", "https://image.url");
		given(kakaoAuthenticationService.authenticate(any(IdToken.class))).willReturn(account);
		given(memberRepository.findByOauthAccount(eq(account))).willReturn(Optional.empty());
		given(kakaoAuthenticationService.getMemberInfoByAccessToken(anyString()))
				.willReturn(memberInfo);
		given(signupService.signup(eq(memberInfo))).willReturn(member);
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		OAuthTokenResponse token =
				loginService.login(
						new LoginRequest(
								idToken,
								"accessToken",
								OAuthProvider.KAKAO,
								"id",
								"model",
								"token"));

		assertThat(token.accessToken()).isEqualTo(target.accessToken());
		assertThat(token.refreshToken()).isEqualTo(target.refreshToken());
	}

	@Test
	@DisplayName("네이버 로그인 시 엑세스 토큰으로 인가받아 로그인 한다.")
	void naverLoginWithoutOidcToken() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String account = "account";
		MemberInfo memberInfo =
				new MemberInfo(
						account, OAuthProvider.NAVER, "name", "test@mail.com", "https://image.url");
		given(naverAuthenticationService.getMemberInfoByAccessToken(anyString()))
				.willReturn(memberInfo);
		given(memberRepository.findByOauthAccount(eq(account))).willReturn(Optional.of(member));
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		OAuthTokenResponse token =
				loginService.login(
						new LoginRequest(
								"", "accessToken", OAuthProvider.NAVER, "id", "model", "token"));

		assertThat(token.accessToken()).isEqualTo(target.accessToken());
		assertThat(token.refreshToken()).isEqualTo(target.refreshToken());
	}

	@Test
	@DisplayName("네이버 로그인 시 인가받은 유저 정보가 없을 경우 회원가입 후 로그인 한다.")
	void naverSignInIfNotExistsMemberInfo() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String account = "account";
		MemberInfo memberInfo =
				new MemberInfo(
						account, OAuthProvider.NAVER, "name", "test@mail.com", "https://image.url");
		given(naverAuthenticationService.getMemberInfoByAccessToken(anyString()))
				.willReturn(memberInfo);
		given(memberRepository.findByOauthAccount(eq(account))).willReturn(Optional.empty());
		given(signupService.signup(eq(memberInfo))).willReturn(member);
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		OAuthTokenResponse token =
				loginService.login(
						new LoginRequest(
								"", "accessToken", OAuthProvider.NAVER, "id", "model", "token"));

		assertThat(token.accessToken()).isEqualTo(target.accessToken());
		assertThat(token.refreshToken()).isEqualTo(target.refreshToken());
	}

	@Test
	@DisplayName("탈퇴한지 14일이 안지난 유저가 로그인 요청을 하면 InvalidRejoinException이 발생한다.")
	void throwInvalidRejoinExceptionWhenLoginIfWithdrawNotBeen14Days() {
		Member member = MemberFixture.DEFAULT.getMember();
		member.delete();
		String account = "account";
		String idToken = "header.payload.signature";
		MemberInfo memberInfo =
				new MemberInfo(
						account, OAuthProvider.KAKAO, "name", "test@mail.com", "https://image.url");
		given(kakaoAuthenticationService.authenticate(any(IdToken.class))).willReturn(account);
		given(memberRepository.findByOauthAccount(eq(account))).willReturn(Optional.of(member));
		given(kakaoAuthenticationService.getMemberInfoByAccessToken(anyString()))
				.willReturn(memberInfo);

		assertThatCode(
						() ->
								loginService.login(
										new LoginRequest(
												idToken,
												"accessToken",
												OAuthProvider.KAKAO,
												"id",
												"model",
												"token")))
				.isInstanceOf(InvalidRejoinException.class)
				.hasMessage(ErrorCode.CAN_NOT_REJOIN.getDescription());
	}

	@Test
	@DisplayName("멤버를 활성화한다.")
	void activateMember() {
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();

		given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(member));

		assertThatCode(() -> loginService.activateMember("abc1234", 1L)).doesNotThrowAnyException();
	}
}
