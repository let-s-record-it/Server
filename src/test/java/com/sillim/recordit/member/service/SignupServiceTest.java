package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

	@Mock MemberRepository memberRepository;
	@Mock CalendarService calendarService;
	@InjectMocks SignupService signupService;

	@Test
	@DisplayName("멤버 데이터를 저장하여 회원가입 시킨다.")
	void signupMember() {
		Member target = MemberFixture.DEFAULT.getMember();
		String account = target.getAuth().getOauthAccount();
		OAuthProvider provider = target.getAuth().getOauthProvider();
		String name = target.getName();
		String profileImageUrl = target.getProfileImageUrl();
		given(memberRepository.save(any(Member.class))).willReturn(target);
		given(calendarService.addCalendar(any(), eq(target.getId())))
				.willReturn(
						Calendar.builder().title("일반").colorHex("ff40d974").member(target).build());

		Member member =
				signupService.signup(new MemberInfo(account, provider, name, profileImageUrl));

		assertThat(member.getAuth().getOauthAccount()).isEqualTo(account);
		assertThat(member.getAuth().getOauthProvider()).isEqualTo(provider);
		assertThat(member.getName()).isEqualTo(name);
		assertThat(member.getProfileImageUrl()).isEqualTo(profileImageUrl);
	}
}
