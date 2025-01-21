package com.sillim.recordit.member.service.google;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.global.exception.member.InvalidIdTokenException;
import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.oidc.IdTokenHeader;
import com.sillim.recordit.member.dto.oidc.IdTokenPayload;
import com.sillim.recordit.member.dto.oidc.OidcPublicKey;
import com.sillim.recordit.member.dto.oidc.OidcPublicKeys;
import com.sillim.recordit.member.dto.oidc.google.GoogleUserInfo;
import com.sillim.recordit.member.dto.request.MemberInfo;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GoogleAuthenticationServiceTest {

	@Mock GoogleOidcClient googleOidcClient;
	@Mock GoogleUserInfoClient googleUserInfoClient;
	@InjectMocks GoogleAuthenticationService googleAuthenticationService;

	@Test
	@DisplayName("인증 토큰을 검증하여 성공하면 멤버 Account를 반환한다.")
	void authenticationToken() {
		OidcPublicKeys publicKeys =
				new OidcPublicKeys(
						List.of(new OidcPublicKey("kid", "alg", "kty", "use", "n", "e")));
		IdToken mockIdToken = mock(IdToken.class);
		String account = "account";
		ReflectionTestUtils.setField(googleAuthenticationService, "appKey", "appKay");
		given(googleOidcClient.getOidcPublicKeys()).willReturn(publicKeys);
		given(mockIdToken.authenticateToken(any(OidcPublicKeys.class), anyString(), anyString()))
				.willReturn(account);

		String memberAccount = googleAuthenticationService.authenticate(mockIdToken);

		assertThat(memberAccount).isEqualTo(account);
	}

	@Test
	@DisplayName("인증 토큰을 검증하여 실패하면 예외가 발생한다.")
	void signupMemberIfNotExists() {
		OidcPublicKeys publicKeys =
				new OidcPublicKeys(
						List.of(
								new OidcPublicKey(
										"kid", "alg", "kty", "use", "modulus", "exponent")));
		IdToken idToken =
				new IdToken(
						new IdTokenHeader("kid", "typ", "alg"),
						new IdTokenPayload("iss", "aud", 123456789L, "sub"),
						"idToken");
		ReflectionTestUtils.setField(googleAuthenticationService, "appKey", "appKay");
		given(googleOidcClient.getOidcPublicKeys()).willReturn(publicKeys);

		Assertions.assertThatThrownBy(() -> googleAuthenticationService.authenticate(idToken))
				.isInstanceOf(InvalidIdTokenException.class);
	}

	@Test
	@DisplayName("access token을 통해 Google User 정보를 가져온다.")
	void getGoogleUserInfoByAccessToken() {
		GoogleUserInfo googleUserInfo =
				new GoogleUserInfo("sub", "name", "name", "picture", "email", true, "ko");
		BDDMockito.given(googleUserInfoClient.getGoogleUserInfo(anyString()))
				.willReturn(googleUserInfo);

		MemberInfo memberInfo =
				googleAuthenticationService.getMemberInfoByAccessToken(
						"accessToken", "pushAlarmToken");

		assertThat(memberInfo.oauthAccount()).isEqualTo(googleUserInfo.sub());
	}
}
