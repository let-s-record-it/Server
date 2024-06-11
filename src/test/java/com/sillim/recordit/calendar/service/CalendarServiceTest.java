package com.sillim.recordit.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.calendar.InvalidCalendarException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
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
	@DisplayName("calendar id를 통해 캘린더를 조회할 수 있다.")
	void searchByCalendarId() {
		Calendar expectedCalendar = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarRepository.findById(eq(1L))).willReturn(Optional.of(expectedCalendar));

		Calendar calendar = calendarService.searchByCalendarId(1L);

		assertThat(calendar).isEqualTo(expectedCalendar);
	}

	@Test
	@DisplayName("calendar id에 맞는 캘린더가 없으면 RecordNotFoundException이 발생한다.")
	void throwRecordNotFoundExceptionIfNotExistsCalendarWhenSearchByCalendarId() {
		given(calendarRepository.findById(eq(1L))).willReturn(Optional.empty());

		assertThatCode(() -> calendarService.searchByCalendarId(1L))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.CALENDAR_NOT_FOUND.getDescription());
	}

	@Test
	@DisplayName("member id를 통해 캘린더 목록을 조회할 수 있다.")
	void searchCalendarsByMemberId() {
		Calendar expectedCalendar1 = CalendarFixture.DEFAULT.getCalendar(member);
		Calendar expectedCalendar2 = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarRepository.findByMemberId(eq(1L)))
				.willReturn(List.of(expectedCalendar1, expectedCalendar2));

		List<Calendar> calendars = calendarService.searchByMemberId(1L);

		assertAll(
				() -> {
					assertThat(calendars).hasSize(2);
					assertThat(calendars.get(0)).isEqualTo(expectedCalendar1);
					assertThat(calendars.get(1)).isEqualTo(expectedCalendar2);
				});
	}

	@Test
	@DisplayName("캘린더를 추가할 수 있다.")
	void addCalendar() {
		Calendar expectedCalendar = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarRepository.save(any(Calendar.class))).willReturn(expectedCalendar);
		given(memberQueryService.findByMemberId(eq(1L))).willReturn(member);

		CalendarAddRequest calendarAddRequest = new CalendarAddRequest("calendar1", "aabbff");
		Calendar calendar = calendarService.addCalendar(calendarAddRequest, 1L);

		assertThat(calendar).isEqualTo(expectedCalendar);
	}

	@Test
	@DisplayName("calendar id와 member id를 통해 캘린더를 삭제할 수 있다.")
	void deleteCalendarByCalendarIdAndMemberId() {
		long calendarId = 1L;
		long memberId = 1L;

		Calendar calendar = mock(Calendar.class);

		given(calendar.equalsMemberId(any())).willReturn(true);
		given(calendarRepository.findById(eq(1L))).willReturn(Optional.of(calendar));

		assertThatCode(() -> calendarService.deleteByCalendarId(calendarId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("calendar의 member id와 현재 인증된 member의 id가 맞지 않으면 InvalidCalendarException이 발생한다.")
	void throwInvalidCalendarExceptionIfCalendarMemberIdNotEqualsAuthorizedMemberId() {
		long calendarId = 1L;
		long memberId = 1L;

		Calendar calendar = mock(Calendar.class);

		given(calendar.equalsMemberId(any())).willReturn(false);
		given(calendarRepository.findById(eq(1L))).willReturn(Optional.of(calendar));

		assertThatCode(() -> calendarService.deleteByCalendarId(calendarId, memberId))
				.isInstanceOf(InvalidCalendarException.class)
				.hasMessage(ErrorCode.INVALID_CALENDAR_DELETE_REQUEST.getDescription());
	}

	@Test
	@DisplayName("캘린더가 해당 사용자의 소유가 아니라면 InvalidMonthlyGoalException을 발생시킨다.")
	void throwInvalidCalendarExceptionIfCalendarIsNotOwnedByMember() {
		long calendarId = 1L;
		long memberId = 1L;

		Calendar calendar = mock(Calendar.class);
		given(calendarRepository.findById(eq(1L))).willReturn(Optional.of(calendar));
		willReturn(false).given(calendar).equalsMemberId(eq(memberId));

		assertThatCode(() -> calendarService.searchByCalendarId(calendarId, memberId))
				.isInstanceOf(InvalidCalendarException.class)
				.hasMessage(ErrorCode.CALENDAR_ACCESS_DENIED.getDescription());
	}
}
