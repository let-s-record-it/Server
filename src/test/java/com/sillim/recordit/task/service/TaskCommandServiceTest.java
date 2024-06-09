package com.sillim.recordit.task.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.dto.TaskAddRequest;
import com.sillim.recordit.task.dto.TaskRepetitionAddRequest;
import com.sillim.recordit.task.fixture.TaskRepetitionPatternFixture;
import com.sillim.recordit.task.repository.TaskRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskCommandServiceTest {

	@InjectMocks TaskCommandService taskCommandService;
	@Mock CalendarService calendarService;
	@Mock TaskGroupService taskGroupService;
	@Mock TaskRepetitionPatternService repetitionPatternService;
	@Mock MemberQueryService memberQueryService;
	@Mock TaskRepository taskRepository;

	private Member member;
	private Calendar calendar;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		calendar = CalendarFixture.DEFAULT.getCalendar(member);
	}

	@Test
	@DisplayName("반복 없는 task를 추가할 수 있다.")
	void addNonRepeatingTaskTest() {
		TaskAddRequest request =
				new TaskAddRequest(
						"회의록 작성",
						"프로젝트 회의록 작성하기",
						LocalDate.of(2024, 1, 1),
						"ff40d974",
						false,
						null,
						null,
						null);
		Long calendarId = 1L;
		Long memberId = 1L;
		TaskGroup taskGroup = new TaskGroup(false, null, null);

		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(taskGroupService.addTaskGroup(eq(false), any(), any(), eq(member)))
				.willReturn(taskGroup);
		given(calendarService.searchByCalendarIdAndMember(eq(calendarId), eq(member)))
				.willReturn(calendar);

		taskCommandService.addTasks(request, calendarId, memberId);

		then(memberQueryService).should(times(1)).findByMemberId(eq(memberId));
		then(taskGroupService).should(times(1)).addTaskGroup(eq(false), any(), any(), eq(member));
		then(calendarService)
				.should(times(1))
				.searchByCalendarIdAndMember(eq(calendarId), eq(member));
		then(taskRepository).should(times(1)).save(any(Task.class));
	}

	@Test
	@DisplayName("반복되는 task들을 추가할 수 있다.")
	void addRepeatingTaskTest() {
		TaskRepetitionAddRequest repetitionRequest =
				new TaskRepetitionAddRequest(
						TaskRepetitionType.DAILY,
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						null);
		TaskAddRequest request =
				new TaskAddRequest(
						"회의록 작성",
						"프로젝트 회의록 작성하기",
						LocalDate.of(2024, 1, 1),
						"ff40d974",
						true,
						repetitionRequest,
						null,
						null);
		Long calendarId = 1L;
		Long memberId = 1L;
		TaskGroup taskGroup = new TaskGroup(true, null, null);
		TaskRepetitionPattern repetitionPattern = TaskRepetitionPatternFixture.DAILY.get(taskGroup);

		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(taskGroupService.addTaskGroup(eq(true), any(), any(), eq(member)))
				.willReturn(taskGroup);
		given(calendarService.searchByCalendarIdAndMember(eq(calendarId), eq(member)))
				.willReturn(calendar);
		given(
						repetitionPatternService.addRepetitionPattern(
								eq(request.repetition()), eq(taskGroup)))
				.willReturn(repetitionPattern);

		taskCommandService.addTasks(request, calendarId, memberId);

		then(memberQueryService).should(times(1)).findByMemberId(eq(memberId));
		then(taskGroupService).should(times(1)).addTaskGroup(eq(true), any(), any(), eq(member));
		then(calendarService)
				.should(times(1))
				.searchByCalendarIdAndMember(eq(calendarId), eq(member));
		then(repetitionPatternService)
				.should(times(1))
				.addRepetitionPattern(eq(request.repetition()), eq(taskGroup));
		then(taskRepository).should(times(91)).save(any(Task.class));
	}
}
