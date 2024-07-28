package com.sillim.recordit.task.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.request.TaskGroupUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskRepetitionUpdateRequest;
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
	@Mock TaskRepository taskRepository;

	private Calendar calendar;

	@BeforeEach
	void init() {
		calendar = mock(Calendar.class);
	}

	@Test
	@DisplayName("반복 없는 task를 추가할 수 있다.")
	void addNonRepeatingTaskTest() {
		TaskGroupUpdateRequest taskGroupRequest = new TaskGroupUpdateRequest(null, null);
		TaskAddRequest request =
				new TaskAddRequest(
						"회의록 작성",
						"프로젝트 회의록 작성하기",
						LocalDate.of(2024, 1, 1),
						"ff40d974",
						false,
						null,
						taskGroupRequest);
		Long calendarId = 1L;
		Long memberId = 1L;
		TaskGroup taskGroup = new TaskGroup(null, null);

		given(taskGroupService.addNonRepeatingTaskGroup(eq(taskGroupRequest), eq(memberId)))
				.willReturn(taskGroup);
		given(calendarService.searchByCalendarId(eq(calendarId))).willReturn(calendar);

		taskCommandService.addTasks(request, calendarId, memberId);

		then(taskRepository).should(times(1)).save(any(Task.class));
	}

	@Test
	@DisplayName("반복되는 task들을 추가할 수 있다.")
	void addRepeatingTaskTest() {
		TaskGroupUpdateRequest taskGroupRequest = new TaskGroupUpdateRequest(null, null);
		TaskRepetitionUpdateRequest repetitionRequest =
				new TaskRepetitionUpdateRequest(
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
						taskGroupRequest);
		Long calendarId = 1L;
		Long memberId = 1L;
		TaskGroup taskGroup = new TaskGroup(null, null);
		taskGroup.setRepetitionPattern(TaskRepetitionPatternFixture.DAILY.get(taskGroup));

		given(
						taskGroupService.addRepeatingTaskGroup(
								eq(taskGroupRequest), eq(repetitionRequest), eq(memberId)))
				.willReturn(taskGroup);
		given(calendarService.searchByCalendarId(eq(calendarId))).willReturn(calendar);

		taskCommandService.addTasks(request, calendarId, memberId);

		then(taskRepository).should(times(91)).save(any(Task.class));
	}
}
