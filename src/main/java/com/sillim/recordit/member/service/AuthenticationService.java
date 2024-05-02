package com.sillim.recordit.member.service;

import com.sillim.recordit.member.dto.oidc.IdToken;
import com.sillim.recordit.member.dto.request.MemberInfo;

public interface AuthenticationService {

	String authenticate(IdToken idToken);

	MemberInfo getMemberInfoByAccessToken(String accessToken);
}
