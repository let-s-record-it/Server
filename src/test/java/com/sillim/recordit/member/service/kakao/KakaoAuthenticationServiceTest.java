package com.sillim.recordit.member.service.kakao;

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
import com.sillim.recordit.member.dto.oidc.kakao.KakaoAccount;
import com.sillim.recordit.member.dto.oidc.kakao.KakaoProfile;
import com.sillim.recordit.member.dto.oidc.kakao.KakaoUserInfo;
import com.sillim.recordit.member.dto.request.MemberInfo;
import java.time.LocalDateTime;
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
class KakaoAuthenticationServiceTest {

	@Mock KakaoOidcClient kakaoOidcClient;
	@Mock KakaoUserInfoClient kakaoUserInfoClient;
	@InjectMocks KakaoAuthenticationService kakaoAuthenticationService;

	@Test
	@DisplayName("인증 토큰을 검증하여 성공하면 멤버를 반환한다.")
	void authenticationToken() {
		OidcPublicKeys publicKeys =
				new OidcPublicKeys(
						List.of(new OidcPublicKey("kid", "alg", "kty", "use", "n", "e")));
		IdToken mockIdToken = mock(IdToken.class);
		String account = "account";
		ReflectionTestUtils.setField(kakaoAuthenticationService, "appKey", "appKay");
		given(kakaoOidcClient.getOidcPublicKeys()).willReturn(publicKeys);
		given(mockIdToken.authenticateToken(any(OidcPublicKeys.class), anyString(), anyString()))
				.willReturn(account);

		String memberAccount = kakaoAuthenticationService.authenticate(mockIdToken);

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
		ReflectionTestUtils.setField(kakaoAuthenticationService, "appKey", "appKay");
		given(kakaoOidcClient.getOidcPublicKeys()).willReturn(publicKeys);

		Assertions.assertThatThrownBy(() -> kakaoAuthenticationService.authenticate(idToken))
				.isInstanceOf(InvalidIdTokenException.class);
	}

	@Test
	@DisplayName("access token을 통해 Kakao User 정보를 가져온다.")
	void getKakaoUserInfoByAccessToken() {
		KakaoUserInfo kakaoUserInfo =
				new KakaoUserInfo(
						1234567L,
						LocalDateTime.now(),
						new KakaoAccount(
								false,
								false,
								false,
								new KakaoProfile("nickname", "image", "image", false, false)));
		BDDMockito.given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
				.willReturn(kakaoUserInfo);

		MemberInfo memberInfo =
				kakaoAuthenticationService.getMemberInfoByAccessToken("accessToken");

		assertThat(memberInfo.oauthAccount()).isEqualTo(kakaoUserInfo.id().toString());
	}
}
