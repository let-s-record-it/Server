package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.request.SignupRequest;

public interface AuthenticationService {

	String BEARER = "Bearer ";

	String authenticate(IdToken idToken);

	SignupRequest getMemberInfoByAccessToken(String accessToken);
}
