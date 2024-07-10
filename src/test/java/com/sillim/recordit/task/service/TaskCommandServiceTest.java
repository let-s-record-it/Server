package com.sillim.recordit.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRemoveStrategy;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.request.TaskRepetitionUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskUpdateRequest;
import com.sillim.recordit.task.fixture.TaskFixture;
import com.sillim.recordit.task.fixture.TaskRepetitionPatternFixture;
import com.sillim.recordit.task.repository.TaskRepository;
import java.time.LocalDate;
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
class TaskCommandServiceTest {

	@InjectMocks TaskCommandService taskCommandService;
	@Mock CalendarService calendarService;
	@Mock TaskGroupService taskGroupService;
	@Mock TaskRepetitionPatternService repetitionPatternService;
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

		given(taskGroupService.addTaskGroup(eq(false), any(), any(), eq(memberId)))
				.willReturn(taskGroup);
		given(calendarService.searchByCalendarId(eq(calendarId), eq(memberId)))
				.willReturn(calendar);

		taskCommandService.addTasks(request, calendarId, memberId);

		then(taskRepository).should(times(1)).save(any(Task.class));
	}

	@Test
	@DisplayName("반복되는 task들을 추가할 수 있다.")
	void addRepeatingTaskTest() {
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
						null,
						null);
		Long calendarId = 1L;
		Long memberId = 1L;
		TaskGroup taskGroup = new TaskGroup(true, null, null);
		TaskRepetitionPattern repetitionPattern = TaskRepetitionPatternFixture.DAILY.get(taskGroup);

		given(taskGroupService.addTaskGroup(eq(true), any(), any(), eq(memberId)))
				.willReturn(taskGroup);
		given(calendarService.searchByCalendarId(eq(calendarId), eq(memberId)))
				.willReturn(calendar);
		given(
						repetitionPatternService.addRepetitionPattern(
								eq(request.repetition()), eq(taskGroup)))
				.willReturn(repetitionPattern);

		taskCommandService.addTasks(request, calendarId, memberId);

		then(taskRepository).should(times(91)).save(any(Task.class));
	}

	@Test
	@DisplayName("선택한 할 일의 할 일 그룹에 속한 모든 할 일을 수정한다.")
	void modifyAllTasksRemoveNothing() {
		Long calendarId = 1L;
		Long newCalendarId = 2L;
		Long taskId = 3L;
		Long taskGroupId = 4L;
		Long memberId = 5L;
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						TaskRemoveStrategy.REMOVE_NOTHING,
						"(수정) 취뽀하기!",
						"(수정) 가즈아",
						LocalDate.of(2024, 7, 10),
						"ff40d974",
						newCalendarId,
						false,
						null,
						null,
						null);

		given(calendarService.searchByCalendarId(eq(calendarId), eq(memberId)))
				.willReturn(calendar);

		TaskGroup taskGroup =
				spy(new TaskGroup(true, mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.of(selectedTask));

		TaskGroup newTaskGroup = new TaskGroup(false, null, null);
		given(
						taskGroupService.modifyTaskGroup(
								eq(taskGroupId), anyBoolean(), any(), any(), eq(memberId)))
				.willReturn(newTaskGroup);

		Calendar newCalendar = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarService.searchByCalendarId(eq(newCalendarId), eq(memberId)))
				.willReturn(newCalendar);

		List<Task> tasksInGroup =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup));
		given(taskRepository.findAllByCalendarIdAndTaskGroupId(eq(calendarId), eq(taskGroupId)))
				.willReturn(tasksInGroup);

		assertThatCode(
						() ->
								taskCommandService.modifyAllTasks(
										request, calendarId, taskId, memberId))
				.doesNotThrowAnyException();
		assertAll(
				() -> {
					assertThat(tasksInGroup.get(0).getTitle()).isEqualTo(request.title());
					assertThat(tasksInGroup.get(0).getDescription())
							.isEqualTo(request.description());
					assertThat(tasksInGroup.get(0).getDate()).isEqualTo(request.date());
					assertThat(tasksInGroup.get(0).getColorHex()).isEqualTo(request.colorHex());
					assertThat(tasksInGroup.get(0).getCalendar()).isEqualTo(newCalendar);
				});
	}

	@Test
	@DisplayName("선택한 할 일의 할 일 그룹에 속한 모든 할 일을 수정한다. - 기존 할 일을 모두 삭제한다.")
	void modifyAllTasksAndRemoveAll() {
		Long calendarId = 1L;
		Long newCalendarId = 2L;
		Long taskId = 3L;
		Long taskGroupId = 4L;
		Long memberId = 5L;
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						TaskRemoveStrategy.REMOVE_ALL,
						"(수정) 취뽀하기!",
						"(수정) 가즈아",
						LocalDate.of(2024, 7, 10),
						"ff40d974",
						newCalendarId,
						false,
						null,
						null,
						null);

		given(calendarService.searchByCalendarId(eq(calendarId), eq(memberId)))
				.willReturn(calendar);

		TaskGroup taskGroup =
				spy(new TaskGroup(true, mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.of(selectedTask));

		TaskGroup newTaskGroup = new TaskGroup(false, null, null);
		given(
						taskGroupService.modifyTaskGroup(
								eq(taskGroupId), anyBoolean(), any(), any(), eq(memberId)))
				.willReturn(newTaskGroup);

		Calendar newCalendar = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarService.searchByCalendarId(eq(newCalendarId), eq(memberId)))
				.willReturn(newCalendar);

		List<Task> tasksInGroup =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 13), calendar, taskGroup));
		given(taskRepository.findAllByCalendarIdAndTaskGroupId(eq(calendarId), eq(taskGroupId)))
				.willReturn(tasksInGroup);

		assertThatCode(
						() ->
								taskCommandService.modifyAllTasks(
										request, calendarId, taskId, memberId))
				.doesNotThrowAnyException();
		then(taskRepository)
				.should(times(1))
				.deleteAllByTaskGroupIdAndTaskIdNot(eq(taskGroupId), eq(taskId));
	}

	@Test
	@DisplayName("선택한 할 일의 할 일 그룹에 속한 모든 할 일을 수정한다. - 달성하지 않은 기존 할 일을 모두 삭제한다.")
	void modifyAllTasksAndRemoveNotAchieved() {
		Long calendarId = 1L;
		Long newCalendarId = 2L;
		Long taskId = 3L;
		Long taskGroupId = 4L;
		Long memberId = 5L;
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						TaskRemoveStrategy.REMOVE_NOT_ACHIEVED,
						"(수정) 취뽀하기!",
						"(수정) 가즈아",
						LocalDate.of(2024, 7, 10),
						"ff40d974",
						newCalendarId,
						false,
						null,
						null,
						null);

		given(calendarService.searchByCalendarId(eq(calendarId), eq(memberId)))
				.willReturn(calendar);

		TaskGroup taskGroup =
				spy(new TaskGroup(false, mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.of(selectedTask));

		TaskGroup newTaskGroup = new TaskGroup(false, null, null);
		given(
						taskGroupService.modifyTaskGroup(
								eq(taskGroupId), anyBoolean(), any(), any(), eq(memberId)))
				.willReturn(newTaskGroup);

		Calendar newCalendar = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarService.searchByCalendarId(eq(newCalendarId), eq(memberId)))
				.willReturn(newCalendar);

		List<Task> tasksInGroup =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 13), calendar, taskGroup));
		given(taskRepository.findAllByCalendarIdAndTaskGroupId(eq(calendarId), eq(taskGroupId)))
				.willReturn(tasksInGroup);

		assertThatCode(
						() ->
								taskCommandService.modifyAllTasks(
										request, calendarId, taskId, memberId))
				.doesNotThrowAnyException();
		then(taskRepository)
				.should(times(1))
				.deleteAllNotAchievedTasksByTaskGroupIdAndTaskIdNot(eq(taskGroupId), eq(taskId));
	}

	@Test
	@DisplayName("선택한 할 일의 할 일 그룹에 속한 모든 할 일을 수정한다. - 반복패턴을 수정한다.")
	void modifyAllTasksWithRepetitionAndRemoveNotAchieved() {
		Long calendarId = 1L;
		Long newCalendarId = 2L;
		Long taskId = 3L;
		Long taskGroupId = 4L;
		Long memberId = 5L;
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						TaskRemoveStrategy.REMOVE_NOT_ACHIEVED,
						"(수정) 취뽀하기!",
						"(수정) 가즈아",
						LocalDate.of(2024, 7, 10),
						"ff40d974",
						newCalendarId,
						false,
						null,
						null,
						null);

		given(calendarService.searchByCalendarId(eq(calendarId), eq(memberId)))
				.willReturn(calendar);

		TaskGroup taskGroup =
				spy(new TaskGroup(false, mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.of(selectedTask));

		TaskGroup newTaskGroup = new TaskGroup(false, null, null);
		given(
						taskGroupService.modifyTaskGroup(
								eq(taskGroupId), anyBoolean(), any(), any(), eq(memberId)))
				.willReturn(newTaskGroup);

		Calendar newCalendar = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarService.searchByCalendarId(eq(newCalendarId), eq(memberId)))
				.willReturn(newCalendar);

		List<Task> tasksInGroup =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 13), calendar, taskGroup));
		given(taskRepository.findAllByCalendarIdAndTaskGroupId(eq(calendarId), eq(taskGroupId)))
				.willReturn(tasksInGroup);

		assertThatCode(
						() ->
								taskCommandService.modifyAllTasks(
										request, calendarId, taskId, memberId))
				.doesNotThrowAnyException();
		then(taskRepository)
				.should(times(1))
				.deleteAllNotAchievedTasksByTaskGroupIdAndTaskIdNot(eq(taskGroupId), eq(taskId));
	}

	@Test
	@DisplayName("선택한 할 일의 할 일 그룹에 속한 모든 할 일을 수정한다. - 반복 패턴을 변경한다.")
	void modifyAllTasksAndModifyRepetitionPattern() {
		Long calendarId = 1L;
		Long newCalendarId = 2L;
		Long taskId = 3L;
		Long taskGroupId = 4L;
		Long memberId = 5L;
		TaskRepetitionUpdateRequest repetitionRequest =
				new TaskRepetitionUpdateRequest(
						TaskRepetitionType.DAILY,
						1,
						LocalDate.of(2024, 6, 1),
						LocalDate.of(2024, 6, 30),
						null,
						null,
						null,
						null,
						null);
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						TaskRemoveStrategy.REMOVE_NOTHING,
						"(수정) 취뽀하기!",
						"(수정) 가즈아",
						LocalDate.of(2024, 6, 10),
						"ff40d974",
						newCalendarId,
						true,
						repetitionRequest,
						null,
						null);

		given(calendarService.searchByCalendarId(eq(calendarId), eq(memberId)))
				.willReturn(calendar);

		TaskGroup taskGroup =
				spy(new TaskGroup(true, mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.of(selectedTask));

		TaskGroup newTaskGroup = new TaskGroup(false, null, null);
		given(
						taskGroupService.modifyTaskGroup(
								eq(taskGroupId), anyBoolean(), any(), any(), eq(memberId)))
				.willReturn(newTaskGroup);

		Calendar newCalendar = CalendarFixture.DEFAULT.getCalendar(member);
		given(calendarService.searchByCalendarId(eq(newCalendarId), eq(memberId)))
				.willReturn(newCalendar);

		List<Task> tasksInGroup =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup));
		given(taskRepository.findAllByCalendarIdAndTaskGroupId(eq(calendarId), eq(taskGroupId)))
				.willReturn(tasksInGroup);

		given(
						repetitionPatternService.modifyRepetitionPattern(
								any(TaskRepetitionUpdateRequest.class), any(TaskGroup.class)))
				.willReturn(
						TaskRepetitionPatternFixture.DAILY.getWithDate(
								LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30), taskGroup));

		assertThatCode(
						() ->
								taskCommandService.modifyAllTasks(
										request, calendarId, taskId, memberId))
				.doesNotThrowAnyException();
		then(taskRepository).should(times(29)).save(any(Task.class));
	}

	@Test
	@DisplayName("선택한 할 일이 존재하지 않는다면 RecordNotFoundException이 발생한다.")
	void throwsRecordNotFoundExceptionIfSelectedTaskIsNotExists() {
		Long calendarId = 1L;
		Long newCalendarId = 2L;
		Long taskId = 3L;
		Long memberId = 4L;
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						TaskRemoveStrategy.REMOVE_NOTHING,
						"(수정) 취뽀하기!",
						"(수정) 가즈아",
						LocalDate.of(2024, 7, 10),
						"ff40d974",
						newCalendarId,
						false,
						null,
						null,
						null);

		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.empty());

		assertThatCode(
						() ->
								taskCommandService.modifyAllTasks(
										request, calendarId, taskId, memberId))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.TASK_NOT_FOUND.getDescription());
	}
}
