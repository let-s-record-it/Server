package com.sillim.recordit.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.service.InviteService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JoinCalendarServiceTest {

	@Mock CalendarMemberService calendarMemberService;
	@Mock InviteService inviteService;
	@InjectMocks JoinCalendarService joinCalendarService;

	@Test
	@DisplayName("캘린더가 삭제되어 있으면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionWhenJoinInCalendarIfCalendarIsDeleted() {
		Member member = MemberFixture.DEFAULT.getMember();
		long memberId = 1L;
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		calendar.delete();
		String inviteCode = "code";
		InviteLink inviteLink = new InviteLink(inviteCode, LocalDateTime.now(), false, calendar);
		given(inviteService.searchInviteInfo(eq(inviteCode))).willReturn(inviteLink);

		assertThatCode(() -> joinCalendarService.joinInCalendar("code", 1L))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_CALENDAR_GET_REQUEST.getDescription());
	}

	@Test
	@DisplayName("초대 코드가 유효하면 캘린더 멤버에 추가된다.")
	void AddedCalendarMemberWhenJoinInCalendarIfInviteCodeIsValid() {
		long calendarId = 1L;
		long joinMemberId = 2L;
		long calendarMemberId = 3L;
		Member member = MemberFixture.DEFAULT.getMember();
		long memberId = 1L;
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = spy(CalendarFixture.DEFAULT.getCalendar(category, memberId));
		String inviteCode = "code";
		InviteLink inviteLink = new InviteLink(inviteCode, LocalDateTime.now(), false, calendar);
		given(calendar.getId()).willReturn(calendarId);
		given(inviteService.searchInviteInfo(eq(inviteCode))).willReturn(inviteLink);
		given(calendarMemberService.addCalendarMember(eq(calendarId), eq(joinMemberId)))
				.willReturn(calendarMemberId);

		Long id = joinCalendarService.joinInCalendar(inviteCode, 2L);

		assertThat(id).isEqualTo(calendarMemberId);
	}
}
