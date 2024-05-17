package com.sillim.recordit.calendar.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.calendar.InvalidCalendarException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

	@Mock CalendarRepository calendarRepository;
	@Mock MemberQueryService memberQueryService;
	@InjectMocks CalendarService calendarService;

	Member member;

	@BeforeEach
	void initObjects() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("calendar id와 member id를 통해 캘린더를 삭제할 수 있다.")
	void deleteCalendarByCalendarIdAndMemberId() {
		long calendarId = 1L;
		long memberId = 1L;
		Member member = mock(Member.class);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);

		given(member.getId()).willReturn(1L);
		given(calendarRepository.findById(eq(1L))).willReturn(Optional.of(calendar));

		assertThatCode(() -> calendarService.deleteByCalendarId(calendarId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("calendar의 member id와 현재 인증된 member의 id가 맞지 않으면 InvalidCalendarException이 발생한다.")
	void throwInvalidCalendarExceptionIfCalendarMemberIdNotEqualsAuthorizedMemberId() {
		long calendarId = 1L;
		long memberId = 1L;

		Member member = mock(Member.class);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(member);

		given(member.getId()).willReturn(2L);
		given(calendarRepository.findById(eq(1L))).willReturn(Optional.of(calendar));

		assertThatCode(() -> calendarService.deleteByCalendarId(calendarId, memberId))
				.isInstanceOf(InvalidCalendarException.class)
				.hasMessage(ErrorCode.INVALID_CALENDAR_DELETE_REQUEST.getDescription());
	}
}
