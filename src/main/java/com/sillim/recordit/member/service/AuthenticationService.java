package com.sillim.recordit.member.service;

import com.sillim.recordit.member.domain.Member;
import java.io.IOException;

public interface AuthenticationService {

	Member authenticateToken(String idToken, String accessToken) throws IOException;
}
