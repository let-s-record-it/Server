package com.sillim.recordit.task.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.fixture.TaskFixture;
import com.sillim.recordit.task.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskQueryServiceTest {

	@InjectMocks TaskQueryService taskQueryService;
	@Mock CalendarService calendarService;
	@Mock TaskRepository taskRepository;

	private Member member;
	private Calendar calendar;
	private TaskGroup taskGroup;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		calendar = spy(CalendarFixture.DEFAULT.getCalendar(member));
		taskGroup = new TaskGroup(false, null, null);
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 날짜의 할 일을 모두 조회한다.")
	void searchAllByCalendarIdAndDate() {
		Long memberId = 1L;
		Long calendarId = 1L;
		LocalDate date = LocalDate.of(2024, 6, 12);
		List<Task> tasks =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup));
		willReturn(calendar).given(calendarService).searchByCalendarId(eq(calendarId));
		given(taskRepository.findAllByCalendarAndDate(calendar, date)).willReturn(tasks);

		taskQueryService.searchAllByDate(calendarId, date, memberId);

		then(calendarService).should(times(1)).searchByCalendarId(eq(calendarId));
		then(taskRepository).should(times(1)).findAllByCalendarAndDate(eq(calendar), eq(date));
	}
}
