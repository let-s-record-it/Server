package com.sillim.recordit.member.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.LoginRequest;
import com.sillim.recordit.member.dto.request.WebLoginRequest;
import com.sillim.recordit.member.dto.response.OAuthTokenResponse;
import com.sillim.recordit.member.service.LoginService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

	private final LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<OAuthTokenResponse> login(@RequestBody LoginRequest loginRequest) throws Exception {
		return ResponseEntity.ok(loginService.login(loginRequest));
	}

	@PostMapping("/web-login")
	public ResponseEntity<OAuthTokenResponse> webLogin(@RequestBody WebLoginRequest webLoginRequest) throws Exception {
		return ResponseEntity.ok(loginService.login(webLoginRequest.exchangeToken()));
	}

	@PostMapping("/activate")
	public ResponseEntity<Void> activateMember(@RequestBody @Valid @NotBlank @Size(max = 10) String personalId,
			@CurrentMember Member member) {
		loginService.activateMember(personalId, member.getId());

		return ResponseEntity.noContent().build();
	}
}
