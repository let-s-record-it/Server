package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.kakao.KakaoIdTokenHeader;
import com.sillim.recordit.member.dto.kakao.KakaoIdTokenPayload;
import com.sillim.recordit.member.dto.kakao.KakaoOidcClient;
import com.sillim.recordit.member.dto.kakao.KakaoOidcPublicKey;
import com.sillim.recordit.member.dto.kakao.KakaoOidePublicKeys;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KakaoAuthenticationServiceTest {

	@Mock ObjectMapper objectMapper;
	@Mock KakaoOidcClient kakaoOidcClient;
	@Mock SignupService signupService;
	@InjectMocks KakaoAuthenticationService kakaoAuthenticationService;

	@Test
	@DisplayName("인증 토큰을 검증하여 성공하면 멤버를 반환한다.")
	void authenticationToken() throws IOException {
		Member target = MemberFixture.DEFAULT.getMember();
		KakaoIdTokenHeader mockIdTokenHeader = mock(KakaoIdTokenHeader.class);
		KakaoIdTokenPayload mockIdTokenPayload = mock(KakaoIdTokenPayload.class);
		KakaoOidePublicKeys publicKeys = new KakaoOidePublicKeys(List.of(
				new KakaoOidcPublicKey("kid", "alg", "kty", "use", "n", "e")));
		String idToken = "header.payload.signature";
		String accessToken = "accessToken";
		String account = "account";
		given(objectMapper.readValue(any(byte[].class), eq(KakaoIdTokenHeader.class)))
				.willReturn(mockIdTokenHeader);
		given(objectMapper.readValue(any(byte[].class), eq(KakaoIdTokenPayload.class)))
				.willReturn(mockIdTokenPayload);
		given(kakaoOidcClient.getKakaoOidcPublicKeys()).willReturn(publicKeys);
		given(signupService.signupIfNotExistsByAccount(anyString(), anyString())).willReturn(
				target);
		given(mockIdTokenPayload.getUserId()).willReturn(account);

		Member member = kakaoAuthenticationService.authenticateToken(idToken, accessToken);

		assertThat(member).isEqualTo(target);
	}
}
