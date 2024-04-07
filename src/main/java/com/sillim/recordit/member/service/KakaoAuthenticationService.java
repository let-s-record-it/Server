package com.sillim.recordit.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.kakao.KakaoIdTokenHeader;
import com.sillim.recordit.member.dto.kakao.KakaoIdTokenPayload;
import com.sillim.recordit.member.dto.kakao.KakaoOidcClient;
import java.io.IOException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoAuthenticationService implements AuthenticationService {

	private final ObjectMapper objectMapper;
	private final KakaoOidcClient kakaoOidcClient;
	private final SignupService signupService;

	@Value("${app-key.kakao}")
	private String appKey;

	@Override
	@Transactional
	public Member authenticateToken(String idToken, String accessToken) throws IOException {
		return signupService.signupIfNotExistsByAccount(authenticateIdToken(idToken), accessToken);
	}

	/**
	 * @author Wonho Seo
	 * @param idToken
	 * @return 인증 성공 시 userId
	 * @throws IOException
	 */
	private String authenticateIdToken(String idToken) throws IOException {
		String[] splittedToken = idToken.split("\\.");
		KakaoIdTokenHeader header =
				objectMapper.readValue(
						Base64.getDecoder().decode(splittedToken[0]), KakaoIdTokenHeader.class);
		KakaoIdTokenPayload payload =
				objectMapper.readValue(
						Base64.getDecoder().decode(splittedToken[1]), KakaoIdTokenPayload.class);

		payload.validatePayload(appKey);
		header.validateSignature(idToken, kakaoOidcClient.getKakaoOidcPublicKeys().keys());

		return payload.getUserId();
	}

}
