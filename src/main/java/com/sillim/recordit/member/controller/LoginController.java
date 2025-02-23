package com.sillim.recordit.member.controller;

import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.member.dto.request.LoginRequest;
import com.sillim.recordit.member.dto.request.WebLoginRequest;
import com.sillim.recordit.member.dto.response.OAuthTokenResponse;
import com.sillim.recordit.member.service.LoginService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

	private final LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<OAuthTokenResponse> login(@RequestBody LoginRequest loginRequest)
			throws IOException {
		AuthorizationToken token = loginService.login(loginRequest);
		return ResponseEntity.ok(new OAuthTokenResponse(token.accessToken(), token.refreshToken()));
	}

	@PostMapping("/web-login")
	public ResponseEntity<OAuthTokenResponse> webLogin(
			@RequestBody WebLoginRequest webLoginRequest) {
		log.info("token: {}", webLoginRequest.exchangeToken());
		AuthorizationToken token = loginService.login(webLoginRequest.exchangeToken());
		return ResponseEntity.ok(new OAuthTokenResponse(token.accessToken(), token.refreshToken()));
	}
}
