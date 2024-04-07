package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.config.security.jwt.JwtProvider;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.request.LoginRequest;
import com.sillim.recordit.member.fixture.AuthorizationTokenFixture;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

	@Mock AuthenticationService authenticationService;
	@Mock JwtProvider jwtProvider;
	@InjectMocks LoginService loginService;

	@Test
	@DisplayName("토큰을 검증하여 로그인을 한다.")
	void login() throws IOException {
		AuthorizationToken target = AuthorizationTokenFixture.DEFAULT.getAuthorizationToken();
		Member member = MemberFixture.DEFAULT.getMember();
		String idToken = "idToken";
		String accessToken = "accessToken";
		OAuthProvider provider = OAuthProvider.KAKAO;
		given(authenticationService.authenticateToken(idToken, accessToken)).willReturn(member);
		given(jwtProvider.generateAuthorizationToken(member.getId())).willReturn(target);

		AuthorizationToken authorizationToken =
				loginService.login(new LoginRequest(idToken, accessToken, provider));

		assertThat(authorizationToken.accessToken()).isEqualTo(target.accessToken());
		assertThat(authorizationToken.refreshToken()).isEqualTo(target.refreshToken());
	}
}
