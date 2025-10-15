package com.sillim.recordit.invite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.dto.response.CalendarMemberResponse;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.domain.InviteLog;
import com.sillim.recordit.invite.domain.InviteState;
import com.sillim.recordit.invite.repository.InviteLinkRepository;
import com.sillim.recordit.invite.repository.InviteLogRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.pushalarm.service.AlarmService;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

	@Mock InviteLinkRepository inviteLinkRepository;
	@Mock CalendarQueryService calendarQueryService;
	@Mock InviteLogRepository inviteLogRepository;
	@Mock CalendarMemberService calendarMemberService;
	@Mock AlarmService alarmService;
	@Mock MemberQueryService memberQueryService;
	@InjectMocks InviteService inviteService;

	@DisplayName("유효한 초대 코드가 있다면 가져온다.")
	@Test
	void getInviteLinkIfExistsValidInviteCode() {
		long calendarId = 1L;
		long memberId = 1L;
		String expectInviteCode = UUID.randomUUID().toString();
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		InviteLink inviteLink =
				new InviteLink(expectInviteCode, LocalDateTime.now().plusDays(7), false, calendar);
		given(inviteLinkRepository.findByCalendarIdAndExpiredIsFalse(eq(calendarId)))
				.willReturn(Optional.of(inviteLink));

		String inviteCode = inviteService.getOrGenerateInviteLink(calendarId);
		String decodedInviteCode = new String(Base64.getUrlDecoder().decode(inviteCode));
		assertThat(decodedInviteCode).isEqualTo(expectInviteCode);
	}

	@DisplayName("유효한 초대 코드가 없다면 새로 생성한다.")
	@Test
	void generateInviteLinkIfNotExistsValidInviteCode() {
		long calendarId = 1L;
		long memberId = 1L;
		String expectInviteCode = UUID.randomUUID().toString();
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		InviteLink expectInviteLink =
				new InviteLink(expectInviteCode, LocalDateTime.now().plusDays(7), false, calendar);
		InviteLink inviteLink =
				new InviteLink(
						UUID.randomUUID().toString(),
						LocalDateTime.now().minusDays(1),
						false,
						calendar);
		given(inviteLinkRepository.findByCalendarIdAndExpiredIsFalse(eq(calendarId)))
				.willReturn(Optional.of(inviteLink));
		given(inviteLinkRepository.save(any(InviteLink.class))).willReturn(expectInviteLink);

		String inviteCode = inviteService.getOrGenerateInviteLink(calendarId);
		String decodedInviteCode = new String(Base64.getUrlDecoder().decode(inviteCode));
		assertThat(decodedInviteCode).isEqualTo(expectInviteCode);
	}

	@DisplayName("초대 코드가 같은 초대 정보가 있다면 가져온다.")
	@Test
	void getInviteInfoIfExistsInviteLinkThatEqualsInviteCode() {
		long memberId = 1L;
		String inviteCode = UUID.randomUUID().toString();
		Member member = spy(MemberFixture.DEFAULT.getMember());
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = spy(CalendarFixture.DEFAULT.getCalendar(category, memberId));
		given(member.getId()).willReturn(1L);
		given(calendar.getId()).willReturn(1L);
		InviteLink expectInviteLink = new InviteLink("code", LocalDateTime.now(), false, calendar);
		given(
						inviteLinkRepository.findInfoByInviteCode(
								eq(new String(Base64.getUrlDecoder().decode(inviteCode)))))
				.willReturn(expectInviteLink);

		InviteLink inviteLink = inviteService.searchInviteInfo(inviteCode);

		assertAll(
				() -> {
					assertThat(inviteLink.getCalendar().getId()).isEqualTo(calendar.getId());
					assertThat(inviteLink.getCalendar().getMemberId()).isEqualTo(member.getId());
				});
	}

	@Test
	@DisplayName("캘린더 초대를 수락한다.")
	void acceptInvite() {
		long inviteLogId = 1L;
		long alarmLogId = 1L;
		long memberId = 1L;
		InviteLog inviteLog = new InviteLog(1L, 1L, 1L, InviteState.WAIT);
		given(inviteLogRepository.findById(eq(inviteLogId))).willReturn(Optional.of(inviteLog));

		inviteService.acceptInvite(inviteLogId, alarmLogId, memberId);

		verify(alarmService, times(1)).deleteAlarm(eq(alarmLogId));
	}

	@Test
	@DisplayName("초대된 멤버가 아니면 초대를 수락할 때 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotInvitedMemberWhenAcceptInvitation() {
		long inviteLogId = 1L;
		long alarmLogId = 1L;
		long memberId = 1L;
		InviteLog inviteLog = new InviteLog(1L, 2L, 1L, InviteState.WAIT);
		given(inviteLogRepository.findById(eq(inviteLogId))).willReturn(Optional.of(inviteLog));

		assertThatCode(() -> inviteService.acceptInvite(inviteLogId, alarmLogId, memberId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}

	@Test
	@DisplayName("캘린더 초대를 거절한다.")
	void rejectInvite() {
		long inviteLogId = 1L;
		long alarmLogId = 1L;
		long memberId = 1L;
		InviteLog inviteLog = new InviteLog(1L, 1L, 1L, InviteState.WAIT);
		given(inviteLogRepository.findById(eq(inviteLogId))).willReturn(Optional.of(inviteLog));

		inviteService.rejectInvite(inviteLogId, alarmLogId, memberId);

		verify(alarmService, times(1)).deleteAlarm(eq(alarmLogId));
	}

	@Test
	@DisplayName("초대된 멤버가 아니면 초대를 거절할 때 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionIfNotInvitedMemberWhenRejectInvitation() {
		long inviteLogId = 1L;
		long alarmLogId = 1L;
		long memberId = 1L;
		InviteLog inviteLog = new InviteLog(1L, 2L, 1L, InviteState.WAIT);
		given(inviteLogRepository.findById(eq(inviteLogId))).willReturn(Optional.of(inviteLog));

		assertThatCode(() -> inviteService.rejectInvite(inviteLogId, alarmLogId, memberId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_REQUEST.getDescription());
	}

	@Test
	@DisplayName("초대하지 않은 팔로우 목록을 가져온다.")
	void searchFollowingsNotInvited() {
		long calendarId = 1L;
		long memberId = 1L;
		String personalId = "test";
		CalendarMemberResponse calendarMember = new CalendarMemberResponse(1L, 2L, "name", "url");
		Member member = mock(Member.class);
		given(calendarMemberService.searchCalendarMembers(eq(calendarId)))
				.willReturn(List.of(calendarMember));
		given(memberQueryService.searchFollowings(eq(personalId))).willReturn(List.of(member));
		given(member.getId()).willReturn(memberId);

		List<Member> followings = inviteService.searchFollowingsNotInvited(calendarId, personalId);

		assertThat(followings).hasSize(1);
		assertThat(followings.get(0).getId()).isEqualTo(1L);
	}
}
