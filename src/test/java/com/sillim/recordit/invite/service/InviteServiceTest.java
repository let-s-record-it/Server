package com.sillim.recordit.invite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.repository.InviteLinkRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.time.LocalDateTime;
import java.util.Base64;
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
	@InjectMocks InviteService inviteService;

	@DisplayName("유효한 초대 코드가 있다면 가져온다.")
	@Test
	void getInviteLinkIfExistsValidInviteCode() {
		long calendarId = 1L;
		String expectInviteCode = UUID.randomUUID().toString();
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member, category);
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
		String expectInviteCode = UUID.randomUUID().toString();
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member, category);
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
		String inviteCode = UUID.randomUUID().toString();
		Member member = spy(MemberFixture.DEFAULT.getMember());
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		Calendar calendar = spy(CalendarFixture.DEFAULT.getCalendar(member, category));
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
					assertThat(inviteLink.getCalendar().getMember().getId())
							.isEqualTo(member.getId());
				});
	}
}
