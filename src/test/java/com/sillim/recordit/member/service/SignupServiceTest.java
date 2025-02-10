package com.sillim.recordit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarCategoryService;
import com.sillim.recordit.calendar.service.CalendarCommandService;
import com.sillim.recordit.category.service.ScheduleCategoryService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.member.dto.request.MemberInfo;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

	@Mock MemberRepository memberRepository;
	@Mock CalendarCommandService calendarCommandService;
	@Mock CalendarCategoryService calendarCategoryService;
	@Mock ScheduleCategoryService scheduleCategoryService;
	@InjectMocks SignupService signupService;

	@Test
	@DisplayName("멤버 데이터를 저장하여 회원가입 시킨다.")
	void signupMember() {
		long memberId = 1L;
		Member target = spy(MemberFixture.DEFAULT.getMember());
		String account = target.getAuth().getOauthAccount();
		OAuthProvider provider = target.getAuth().getOauthProvider();
		String name = target.getName();
		String profileImageUrl = target.getProfileImageUrl();
		given(target.getId()).willReturn(memberId);
		given(memberRepository.save(any(Member.class))).willReturn(target);
		given(calendarCategoryService.addDefaultCategories(eq(memberId)))
				.willReturn(List.of(1L, 2L, 3L, 4L));
		given(calendarCommandService.addCalendar(any(), eq(memberId)))
				.willReturn(Calendar.builder().title("일반").member(target).build());

		Member member =
				signupService.signup(new MemberInfo(account, provider, name, profileImageUrl));

		assertThat(member.getAuth().getOauthAccount()).isEqualTo(account);
		assertThat(member.getAuth().getOauthProvider()).isEqualTo(provider);
		assertThat(member.getName()).isEqualTo(name);
		assertThat(member.getProfileImageUrl()).isEqualTo(profileImageUrl);
	}
}
