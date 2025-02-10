package com.sillim.recordit.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.request.TaskGroupUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskRepetitionUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskUpdateRequest;
import com.sillim.recordit.task.fixture.TaskFixture;
import com.sillim.recordit.task.fixture.TaskRepetitionPatternFixture;
import com.sillim.recordit.task.repository.TaskRepository;
import java.time.LocalDate;
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
	@Mock CalendarQueryService calendarQueryService;
	@Mock TaskGroupService taskGroupService;
	@Mock TaskRepository taskRepository;

	private CalendarCategory category;
	private Calendar calendar;
	private Member member;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		calendar = CalendarFixture.DEFAULT.getCalendar(member, category);
	}

	@Test
	@DisplayName("반복 없는 task를 추가할 수 있다.")
	void addNonRepeatingTaskTest() {
		TaskGroupUpdateRequest taskGroupRequest = mock(TaskGroupUpdateRequest.class);
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
		calendar = spy(calendar);
		willDoNothing().given(calendar).validateAuthenticatedMember(anyLong());

		given(taskGroupService.addNonRepeatingTaskGroup(eq(taskGroupRequest), eq(memberId)))
				.willReturn(taskGroup);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);

		taskCommandService.addTasks(request, calendarId, memberId);

		then(taskRepository).should(times(1)).save(any(Task.class));
	}

	@Test
	@DisplayName("반복되는 task들을 추가할 수 있다.")
	void addRepeatingTaskTest() {
		TaskGroupUpdateRequest taskGroupRequest = mock(TaskGroupUpdateRequest.class);
		TaskRepetitionUpdateRequest repetitionRequest = mock(TaskRepetitionUpdateRequest.class);
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
		calendar = spy(calendar);
		willDoNothing().given(calendar).validateAuthenticatedMember(anyLong());

		given(
						taskGroupService.addRepeatingTaskGroup(
								eq(taskGroupRequest), eq(repetitionRequest), eq(memberId)))
				.willReturn(taskGroup);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);

		taskCommandService.addTasks(request, calendarId, memberId);

		then(taskRepository).should(times(91)).save(any(Task.class));
	}

	@Test
	@DisplayName("할 일 전체를 수정하는 경우, 기존 할 일 그룹들을 모두 삭제하고 변경사항에 맞게 다시 생성한다.")
	void resetTaskGroupAndAddNewTasks() {
		Long calendarId = 1L;
		Long selectedTaskId = 2L;
		Long memberId = 3L;
		Long taskGroupId = 4L;
		Long newCalendarId = 5L;

		TaskRepetitionUpdateRequest repetitionRequest = mock(TaskRepetitionUpdateRequest.class);
		given(repetitionRequest.repetitionStartDate()).willReturn(LocalDate.of(2024, 1, 1));
		TaskGroupUpdateRequest taskGroupRequest = mock(TaskGroupUpdateRequest.class);
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						"(수정) 회의록 작성",
						"(수정) 프로젝트 회의록 작성하기",
						LocalDate.of(2024, 1, 10),
						"ff40d974",
						newCalendarId,
						true,
						repetitionRequest,
						taskGroupRequest);

		calendar = spy(calendar);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		willDoNothing().given(calendar).validateAuthenticatedMember(anyLong());

		TaskGroup taskGroup = spy(new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(anyLong(), anyLong()))
				.willReturn(Optional.of(selectedTask));
		willDoNothing().given(taskRepository).deleteAllByTaskGroupId(anyLong());

		Calendar newCalendar = spy(CalendarFixture.DEFAULT.getCalendar(member, category));
		given(calendarQueryService.searchByCalendarId(eq(newCalendarId))).willReturn(newCalendar);
		willDoNothing().given(newCalendar).validateAuthenticatedMember(anyLong());

		TaskGroup newTaskGroup = new TaskGroup(null, null);
		TaskRepetitionPattern newRepetitionPattern =
				TaskRepetitionPatternFixture.DAILY.get(taskGroup);
		newTaskGroup.setRepetitionPattern(newRepetitionPattern);

		given(
						taskGroupService.modifyTaskGroupAndMakeRepeatable(
								anyLong(),
								any(TaskGroupUpdateRequest.class),
								any(TaskRepetitionUpdateRequest.class),
								anyLong()))
				.willReturn(newTaskGroup);

		taskCommandService.resetTaskGroupAndAddNewTasks(
				request, calendarId, selectedTaskId, memberId);

		then(taskRepository).should(times(1)).deleteAllByTaskGroupId(anyLong());
		then(taskRepository).should(times(91)).save(any(Task.class));
	}

	@Test
	@DisplayName("선택한 할 일 하나를 수정한다.")
	void modifyOne() {
		Long calendarId = 1L;
		Long selectedTaskId = 2L;
		Long memberId = 3L;
		Long taskGroupId = 4L;
		Long newCalendarId = 5L;

		TaskGroupUpdateRequest taskGroupRequest = mock(TaskGroupUpdateRequest.class);
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						"(수정) 회의록 작성",
						"(수정) 프로젝트 회의록 작성하기",
						LocalDate.of(2024, 1, 10),
						"ff40d974",
						newCalendarId,
						false,
						null,
						taskGroupRequest);

		calendar = spy(calendar);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		willDoNothing().given(calendar).validateAuthenticatedMember(anyLong());

		TaskGroup taskGroup = spy(new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(anyLong(), anyLong()))
				.willReturn(Optional.of(selectedTask));

		Calendar newCalendar = spy(CalendarFixture.DEFAULT.getCalendar(member, category));
		given(calendarQueryService.searchByCalendarId(eq(newCalendarId))).willReturn(newCalendar);
		willDoNothing().given(newCalendar).validateAuthenticatedMember(anyLong());

		TaskGroup newTaskGroup = new TaskGroup(null, null);

		given(
						taskGroupService.modifyTaskGroup(
								anyLong(), any(TaskGroupUpdateRequest.class), anyLong()))
				.willReturn(newTaskGroup);

		taskCommandService.modifyOne(request, calendarId, selectedTaskId, memberId);

		assertAll(
				() -> {
					assertThat(selectedTask.getTitle()).isEqualTo(request.newTitle());
					assertThat(selectedTask.getDescription()).isEqualTo(request.newDescription());
					assertThat(selectedTask.getDate()).isEqualTo(request.date());
					assertThat(selectedTask.getColorHex()).isEqualTo(request.newColorHex());
					assertThat(selectedTask.getCalendar()).isEqualTo(newCalendar);
					assertThat(selectedTask.getTaskGroup().getMonthlyGoal()).isEmpty();
					assertThat(selectedTask.getTaskGroup().getWeeklyGoal()).isEmpty();
				});
	}

	@Test
	@DisplayName("선택한 할 일의 수정사항을 반영하여 새로운 반복 패턴에 맞게 할 일들을 추가한다.")
	void modifyOneAndMakeRepeatable() {
		Long calendarId = 1L;
		Long selectedTaskId = 2L;
		Long memberId = 3L;
		Long taskGroupId = 4L;
		Long newCalendarId = 5L;

		TaskRepetitionUpdateRequest repetitionRequest = mock(TaskRepetitionUpdateRequest.class);
		given(repetitionRequest.repetitionStartDate()).willReturn(LocalDate.of(2024, 1, 1));
		TaskGroupUpdateRequest taskGroupRequest = mock(TaskGroupUpdateRequest.class);
		TaskUpdateRequest request =
				new TaskUpdateRequest(
						"(수정) 회의록 작성",
						"(수정) 프로젝트 회의록 작성하기",
						LocalDate.of(2024, 1, 10),
						"ff40d974",
						newCalendarId,
						true,
						repetitionRequest,
						taskGroupRequest);

		calendar = spy(calendar);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		willDoNothing().given(calendar).validateAuthenticatedMember(anyLong());

		TaskGroup taskGroup = spy(new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(anyLong(), anyLong()))
				.willReturn(Optional.of(selectedTask));

		Calendar newCalendar = spy(CalendarFixture.DEFAULT.getCalendar(member, category));
		given(calendarQueryService.searchByCalendarId(eq(newCalendarId))).willReturn(newCalendar);
		willDoNothing().given(newCalendar).validateAuthenticatedMember(anyLong());

		TaskGroup newTaskGroup = new TaskGroup(null, null);
		newTaskGroup.setRepetitionPattern(TaskRepetitionPatternFixture.DAILY.get(taskGroup));

		given(
						taskGroupService.modifyTaskGroupAndMakeRepeatable(
								anyLong(),
								any(TaskGroupUpdateRequest.class),
								any(TaskRepetitionUpdateRequest.class),
								anyLong()))
				.willReturn(newTaskGroup);

		taskCommandService.modifyOne(request, calendarId, selectedTaskId, memberId);

		then(taskRepository).should(times(91)).save(any(Task.class));
	}

	@Test
	@DisplayName("선택한 할 일이 속한 할 일 그룹 내의 모든 할 일들을 삭제한다.")
	void removeAll() {
		Long calendarId = 1L;
		Long selectedTaskId = 2L;
		Long memberId = 3L;
		Long taskGroupId = 4L;

		calendar = spy(calendar);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		willDoNothing().given(calendar).validateAuthenticatedMember(anyLong());

		TaskGroup taskGroup = spy(new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(anyLong(), anyLong()))
				.willReturn(Optional.of(selectedTask));

		taskCommandService.removeAll(calendarId, selectedTaskId, memberId);

		then(taskRepository).should(times(1)).deleteAllByTaskGroupId(anyLong());
	}

	@Test
	@DisplayName("선택한 할 일이 속한 할 일 그룹 내의 모든 할 일 중 선택한 날짜 이후의 할 일을 삭제한다.")
	void removeAfterAll() {
		Long calendarId = 1L;
		Long selectedTaskId = 2L;
		Long memberId = 3L;
		Long taskGroupId = 4L;

		calendar = spy(calendar);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		willDoNothing().given(calendar).validateAuthenticatedMember(anyLong());

		TaskGroup taskGroup = spy(new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class)));
		given(taskGroup.getId()).willReturn(taskGroupId);

		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(anyLong(), anyLong()))
				.willReturn(Optional.of(selectedTask));

		taskCommandService.removeAllAfterDate(calendarId, selectedTaskId, memberId);

		then(taskRepository)
				.should(times(1))
				.deleteAllByTaskGroupIdAndDateAfterOrEqual(anyLong(), any(LocalDate.class));
	}

	@Test
	@DisplayName("선택한 할 일을 삭제한다.")
	void removeOne() {
		Long calendarId = 1L;
		Long selectedTaskId = 2L;
		Long memberId = 3L;

		calendar = spy(calendar);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		willDoNothing().given(calendar).validateAuthenticatedMember(anyLong());

		TaskGroup taskGroup = new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class));
		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		given(taskRepository.findByIdAndCalendarId(anyLong(), anyLong()))
				.willReturn(Optional.of(selectedTask));

		taskCommandService.removeOne(calendarId, selectedTaskId, memberId);

		assertThat(selectedTask.isDeleted()).isTrue();
	}
}
