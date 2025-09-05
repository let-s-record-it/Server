package com.sillim.recordit.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarModifyRequest;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.category.service.ScheduleCategoryCommandService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.calendar.InvalidCalendarException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.schedule.service.ScheduleCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarCommandServiceTest {

	@Mock CalendarRepository calendarRepository;
	@Mock CalendarQueryService calendarQueryService;
	@Mock CalendarMemberService calendarMemberService;
	@Mock CalendarCategoryQueryService calendarCategoryQueryService;
	@Mock ScheduleCommandService scheduleCommandService;
	@Mock ScheduleCategoryCommandService scheduleCategoryCommandService;
	@InjectMocks CalendarCommandService calendarCommandService;

	Member member;

	@BeforeEach
	void initObjects() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("캘린더를 추가할 수 있다.")
	void addCalendar() {
		long memberId = 1L;
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar expectedCalendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		given(calendarRepository.save(any(Calendar.class))).willReturn(expectedCalendar);

		CalendarAddRequest calendarAddRequest = new CalendarAddRequest("calendar1", 1L);
		Calendar calendar = calendarCommandService.addCalendar(calendarAddRequest, 1L);

		assertThat(calendar).isEqualTo(expectedCalendar);
	}

	@Test
	@DisplayName("캘린더를 수정할 수 있다.")
	void modifyCalendar() {
		Calendar calendar = mock(Calendar.class);
		given(calendar.isOwnedBy(any())).willReturn(true);
		given(calendarQueryService.searchByCalendarId(eq(1L))).willReturn(calendar);

		CalendarModifyRequest calendarModifyRequest = new CalendarModifyRequest("calendar1", 1L);

		assertThatCode(() -> calendarCommandService.modifyCalendar(calendarModifyRequest, 1L, 1L))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("calendar id와 member id를 통해 캘린더를 삭제할 수 있다.")
	void deleteCalendarByCalendarIdAndMemberId() {
		long calendarId = 1L;
		long memberId = 1L;

		Calendar calendar = mock(Calendar.class);

		given(calendar.isOwnedBy(any())).willReturn(true);
		given(calendarQueryService.searchByCalendarId(eq(1L))).willReturn(calendar);

		assertThatCode(() -> calendarCommandService.removeByCalendarId(calendarId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("calendar의 member id와 현재 인증된 member의 id가 맞지 않으면 InvalidCalendarException이 발생한다.")
	void throwInvalidCalendarExceptionIfCalendarMemberIdNotEqualsAuthorizedMemberId() {
		long calendarId = 1L;
		long memberId = 1L;

		Calendar calendar = mock(Calendar.class);

		given(calendar.isOwnedBy(any())).willReturn(false);
		given(calendarQueryService.searchByCalendarId(eq(1L))).willReturn(calendar);

		assertThatCode(() -> calendarCommandService.removeByCalendarId(calendarId, memberId))
				.isInstanceOf(InvalidCalendarException.class)
				.hasMessage(ErrorCode.INVALID_CALENDAR_GET_REQUEST.getDescription());
	}
}
