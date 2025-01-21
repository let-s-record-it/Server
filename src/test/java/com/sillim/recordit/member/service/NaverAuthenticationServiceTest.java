package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import com.sillim.recordit.member.dto.oidc.naver.NaverProfile;
import com.sillim.recordit.member.dto.oidc.naver.NaverUserInfo;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.service.naver.NaverAuthenticationService;
import com.sillim.recordit.member.service.naver.NaverUserInfoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NaverAuthenticationServiceTest {

	@Mock NaverUserInfoClient naverUserInfoClient;
	@InjectMocks NaverAuthenticationService naverAuthenticationService;

	@Test
	@DisplayName("access token을 통해 Naver User 정보를 가져온다.")
	void getNaverUserInfoByAccessToken() {
		NaverUserInfo naverUserInfo =
				new NaverUserInfo(
						"00",
						"success",
						new NaverProfile("id", "nickname", "image", "email", "name"));
		BDDMockito.given(naverUserInfoClient.getNaverUserInfo(anyString()))
				.willReturn(naverUserInfo);

		MemberInfo memberInfo =
				naverAuthenticationService.getMemberInfoByAccessToken(
						"accessToken", "pushAlarmToken");

		assertThat(memberInfo.oauthAccount()).isEqualTo(naverUserInfo.response().id());
	}
}
