package com.sillim.recordit.member.controller;

import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentRequest;
import static com.sillim.recordit.support.restdocs.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.request.LoginRequest;
import com.sillim.recordit.member.service.LoginService;
import com.sillim.recordit.support.restdocs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LoginController.class)
class LoginControllerTest extends RestDocsTest {

	@MockBean LoginService loginService;

	@Test
	@DisplayName("인가 받은 토큰으로 로그인을 한다.")
	void loginWithToken() throws Exception {
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";
		LoginRequest loginRequest = new LoginRequest("idToken", accessToken, OAuthProvider.KAKAO);
		AuthorizationToken token = new AuthorizationToken(accessToken, refreshToken);
		given(loginService.login(loginRequest)).willReturn(token);

		ResultActions perform =
				mockMvc.perform(
						post("/api/v1/login")
								.contentType(MediaType.APPLICATION_JSON)
								.content(toJson(loginRequest)));

		perform.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").value(accessToken))
				.andExpect(jsonPath("$.refreshToken").value(refreshToken));

		perform.andDo(print())
				.andDo(document("login-with-token", getDocumentRequest(), getDocumentResponse()));
	}
}
