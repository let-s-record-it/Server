package com.sillim.recordit.testlogin;

import com.sillim.recordit.config.security.jwt.AuthorizationToken;
import com.sillim.recordit.config.security.jwt.JwtProvider;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.dto.response.OAuthTokenResponse;
import com.sillim.recordit.member.repository.MemberRepository;
import com.sillim.recordit.member.service.LoginService;
import com.sillim.recordit.member.service.SignupService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test-login")
public class TestLoginController {

	private final SignupService signupService;
	private final JwtProvider jwtProvider;
	private final LoginService loginService;
	private final MemberRepository memberRepository;

	@PostMapping
	public ResponseEntity<OAuthTokenResponse> testLogin(
			@RequestBody TestLoginRequest testLoginRequest) throws Exception {
		log.info("test login request : {}", testLoginRequest);
		Optional<Member> memberOptional = memberRepository.findByEmail(testLoginRequest.email());
		Member member;
		if (memberOptional.isEmpty()) {
			MemberInfo memberInfo =
					MemberInfo.builder()
							.oauthAccount(UUID.randomUUID().toString())
							.oAuthProvider(OAuthProvider.KAKAO)
							.name(testLoginRequest.name())
							.email(testLoginRequest.email())
							.profileImageUrl("")
							.build();
			member = signupService.signup(memberInfo);
			loginService.activateMember(testLoginRequest.personalId(), member.getId());
		} else {
			member = memberOptional.get();
		}
		AuthorizationToken token = jwtProvider.generateAuthorizationToken(member.getEmail());
		return ResponseEntity.ok(
				new OAuthTokenResponse(token.accessToken(), token.refreshToken(), true));
	}
}
