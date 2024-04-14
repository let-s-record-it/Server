package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.config.security.jwt.JwtProvider;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.request.LoginRequest;
import com.sillim.recordit.member.fixture.AuthorizationTokenFixture;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.repository.MemberRepository;
import com.sillim.recordit.member.service.kakao.KakaoAuthenticationService;
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
	@Mock JwtProvider jwtProvider;
	@Mock MemberRepository memberRepository;
	@Mock ObjectMapper objectMapper;
	@InjectMocks LoginService loginService;

	@Test
	@DisplayName("토큰을 검증하여 로그인을 한다.")
	void login() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String idToken = "header.payload.signature";
		String account = "account";
		given(kakaoAuthenticationService.authenticate(any(IdToken.class))).willReturn(account);
		given(memberRepository.findByAuthOauthAccount(eq(account))).willReturn(Optional.of(member));
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		AuthorizationToken authorizationToken =
				loginService.login(new LoginRequest(idToken, "accessToken", OAuthProvider.KAKAO));

		assertThat(authorizationToken.accessToken()).isEqualTo(target.accessToken());
		assertThat(authorizationToken.refreshToken()).isEqualTo(target.refreshToken());
	}
}
