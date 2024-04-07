package com.sillim.recordit.member.service;

import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.config.security.jwt.JwtProvider;
import com.sillim.recordit.member.dto.request.LoginRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

	private final AuthenticationService authenticationService;
	private final JwtProvider jwtProvider;

	@Transactional
	public AuthorizationToken login(LoginRequest loginRequest) throws IOException {
		return jwtProvider.generateAuthorizationToken(authenticationService
				.authenticateToken(loginRequest.idToken(), loginRequest.accessToken()).getId());
	}
}
