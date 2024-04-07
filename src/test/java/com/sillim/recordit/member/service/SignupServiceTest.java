package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.kakao.KakaoAccount;
import com.sillim.recordit.member.dto.kakao.KakaoProfile;
import com.sillim.recordit.member.dto.kakao.KakaoUserInfo;
import com.sillim.recordit.member.dto.request.SignupRequest;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

	@Mock MemberRepository memberRepository;
	@Mock RestTemplate restTemplate;
	@InjectMocks SignupService signupService;

	@Test
	@DisplayName("멤버 데이터를 저장하여 회원가입 시킨다.")
	void signupMember() {
		Member target = MemberFixture.DEFAULT.getMember();
		String account = target.getAuth().getOauthAccount();
		OAuthProvider provider = target.getAuth().getOauthProvider();
		String name = target.getName();
		given(memberRepository.save(any(Member.class))).willReturn(target);

		Member member = signupService.signup(new SignupRequest(account, provider, name));

		assertThat(member.getAuth().getOauthAccount()).isEqualTo(account);
		assertThat(member.getAuth().getOauthProvider()).isEqualTo(provider);
		assertThat(member.getName()).isEqualTo(name);
	}

	@Test
	@DisplayName("멤버 데이터가 없으면 access Token을 통해 회원가입 시킨다.")
	void signupIfNotExistsMember() {
		Member target = MemberFixture.DEFAULT.getMember();
		given(memberRepository.findByAuthOauthAccount(anyString())).willReturn(Optional.empty());
		given(memberRepository.save(any(Member.class))).willReturn(target);
		KakaoUserInfo kakaoUserInfo =
				new KakaoUserInfo(
						1L,
						LocalDateTime.now(),
						new KakaoAccount(
								false,
								false,
								false,
								new KakaoProfile("nickname", "url", "url", false, false)));
		given(restTemplate.exchange(any(RequestEntity.class), eq(KakaoUserInfo.class)))
				.willReturn(ResponseEntity.ok(kakaoUserInfo));

		Member member = signupService.signupIfNotExistsByAccount("account", "accessToken");

		assertThat(member).isEqualTo(target);
	}
}
