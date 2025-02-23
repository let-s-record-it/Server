package com.sillim.recordit.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.repository.CalendarMemberRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarMemberServiceTest {

	@Mock CalendarMemberRepository calendarMemberRepository;
	@Mock CalendarQueryService calendarQueryService;
	@Mock MemberQueryService memberQueryService;
	@InjectMocks CalendarMemberService calendarMemberService;

	@Test
	@DisplayName("캘린더 멤버를 조회할 수 있다.")
	void searchCalendarMemberIfCalendarOwner() {
		long calendarId = 1L;
		long memberId = 1L;
		Member member = spy(MemberFixture.DEFAULT.getMember());
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		Calendar calendar = spy(CalendarFixture.DEFAULT.getCalendar(member, category));
		CalendarMember expectCalendarMember = new CalendarMember(member, calendar);
		given(calendarMemberRepository.findCalendarMember(eq(calendarId), eq(memberId)))
				.willReturn(Optional.of(expectCalendarMember));

		CalendarMember calendarMember =
				calendarMemberService.searchCalendarMember(calendarId, memberId);

		assertThat(calendarMember).isEqualTo(expectCalendarMember);
	}

	@Test
	@DisplayName("캘린더 멤버가 존재하지 않으면 RecordNotFoundException이 발생한다.")
	void throwRecordNotFoundExceptionWhenSearchCalendarMemberIfNotExistsCalendarMember() {
		long calendarId = 1L;
		long memberId = 1L;
		given(calendarMemberRepository.findCalendarMember(eq(calendarId), eq(memberId)))
				.willReturn(Optional.empty());

		assertThatCode(() -> calendarMemberService.searchCalendarMember(calendarId, memberId))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.CALENDAR_MEMBER_NOT_FOUND.getDescription());
	}

	@Test
	@DisplayName("캘린더 멤버를 추가할 수 있다.")
	void addCalendarMember() {
		long memberId = 1L;
		long calendarId = 1L;
		long calendarMemberId = 1L;
		Member member = spy(MemberFixture.DEFAULT.getMember());
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		Calendar calendar = spy(CalendarFixture.DEFAULT.getCalendar(member, category));
		CalendarMember expectCalendarMember = mock(CalendarMember.class);
		given(expectCalendarMember.getId()).willReturn(calendarMemberId);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(calendarMemberRepository.save(any(CalendarMember.class)))
				.willReturn(expectCalendarMember);

		Long savedCalendarMemberId = calendarMemberService.addCalendarMember(calendarId, memberId);

		assertThat(savedCalendarMemberId).isEqualTo(calendarMemberId);
	}

	@Test
	@DisplayName("캘린더 소유자면 해당 캘린더 멤버를 삭제할 수 있다.")
	void removeCalendarMemberIfCalendarOwner() {
		long memberId = 1L;
		long calendarId = 1L;
		long ownerId = 2L;
		Member owner = mock(Member.class);
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(owner);
		Calendar calendar = spy(CalendarFixture.DEFAULT.getCalendar(owner, category));
		CalendarMember calendarMember = new CalendarMember(owner, calendar);
		given(owner.equalsId(eq(ownerId))).willReturn(true);
		given(calendarMemberRepository.findCalendarMember(eq(calendarId), eq(memberId)))
				.willReturn(Optional.of(calendarMember));
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);

		assertThatCode(
						() ->
								calendarMemberService.removeCalendarMember(
										calendarId, memberId, ownerId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("캘린더 소유자가 아니면 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionWhenRemoveCalendarMemberIfNotCalendarOwner() {
		long memberId = 1L;
		long calendarId = 1L;
		long ownerId = 2L;
		Member owner = mock(Member.class);
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(owner);
		Calendar calendar = spy(CalendarFixture.DEFAULT.getCalendar(owner, category));
		given(owner.equalsId(eq(ownerId))).willReturn(false);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);

		assertThatCode(
						() ->
								calendarMemberService.removeCalendarMember(
										calendarId, memberId, ownerId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_CALENDAR_MEMBER_GET_REQUEST.getDescription());
	}
}
