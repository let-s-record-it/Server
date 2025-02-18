package com.sillim.recordit.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
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
class CalendarQueryServiceTest {

	@Mock CalendarRepository calendarRepository;
	@InjectMocks CalendarQueryService calendarQueryService;

	Member member;

	@BeforeEach
	void initObjects() {
		member = MemberFixture.DEFAULT.getMember();
	}

	@Test
	@DisplayName("calendar id를 통해 캘린더를 조회할 수 있다.")
	void searchByCalendarId() {
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		Calendar expectedCalendar = CalendarFixture.DEFAULT.getCalendar(member, category);
		given(calendarRepository.findByIdWithFetchCategory(eq(1L)))
				.willReturn(Optional.of(expectedCalendar));

		Calendar calendar = calendarQueryService.searchByCalendarId(1L);

		assertThat(calendar).isEqualTo(expectedCalendar);
	}

	@Test
	@DisplayName("calendar id에 맞는 캘린더가 없으면 RecordNotFoundException이 발생한다.")
	void throwRecordNotFoundExceptionIfNotExistsCalendarWhenSearchByCalendarId() {
		given(calendarRepository.findByIdWithFetchCategory(eq(1L))).willReturn(Optional.empty());

		assertThatCode(() -> calendarQueryService.searchByCalendarId(1L))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.CALENDAR_NOT_FOUND.getDescription());
	}

	@Test
	@DisplayName("member id를 통해 캘린더 목록을 조회할 수 있다.")
	void searchCalendarsByMemberId() {
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		Calendar expectedCalendar1 = CalendarFixture.DEFAULT.getCalendar(member, category);
		Calendar expectedCalendar2 = CalendarFixture.DEFAULT.getCalendar(member, category);
		given(calendarRepository.findByMemberId(eq(1L)))
				.willReturn(List.of(expectedCalendar1, expectedCalendar2));

		List<Calendar> calendars = calendarQueryService.searchByMemberId(1L);

		assertAll(
				() -> {
					assertThat(calendars).hasSize(2);
					assertThat(calendars.get(0)).isEqualTo(expectedCalendar1);
					assertThat(calendars.get(1)).isEqualTo(expectedCalendar2);
				});
	}
}
