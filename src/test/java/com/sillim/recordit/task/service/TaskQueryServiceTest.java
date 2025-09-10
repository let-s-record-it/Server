package com.sillim.recordit.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.dto.response.TaskDetailsResponse;
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
class TaskQueryServiceTest {

	@InjectMocks TaskQueryService taskQueryService;
	@Mock CalendarQueryService calendarQueryService;
	@Mock TaskRepository taskRepository;

	private Member member;
	private Calendar calendar;
	private ScheduleCategory taskCategory;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		calendar = mock(Calendar.class);
		taskCategory = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 날짜의 할 일을 모두 조회한다.")
	void searchAllByCalendarIdAndDate() {
		Long memberId = 1L;
		Long calendarId = 2L;
		TaskGroup taskGroup = new TaskGroup(null, null);
		LocalDate date = LocalDate.of(2024, 6, 12);
		List<Task> tasks =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), taskCategory, calendar, taskGroup));
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(taskRepository.findAllByCalendarIdAndDate(calendarId, date)).willReturn(tasks);

		List<Task> found = taskQueryService.searchAllByDate(calendarId, date, memberId);

		assertThat(found).hasSize(tasks.size());
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 id의 할 일을 조회하여 DTO를 반환한다. (반복 없음)")
	void searchByIdAndCalendarIdWithNoRepeat() {
		Long memberId = 1L;
		Long calendarId = 2L;
		TaskGroup taskGroup = new TaskGroup(null, null);
		Long taskId = 3L;
		Task task = spy(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		given(task.getId()).willReturn(taskId);

		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.of(task));

		TaskDetailsResponse taskDto =
				taskQueryService.searchByIdAndCalendarId(taskId, calendarId, memberId);

		assertAll(
				() -> {
					assertThat(taskDto.taskId()).isEqualTo(task.getId());
					assertThat(taskDto.title()).isEqualTo(task.getTitle());
					assertThat(taskDto.description()).isEqualTo(task.getDescription());
					assertThat(taskDto.date()).isEqualTo(task.getDate());
					assertThat(taskDto.colorHex()).isEqualTo(task.getColorHex());
					assertThat(taskDto.isRepeated()).isFalse();
					assertThat(taskDto.repetition()).isNull();
					assertThat(taskDto.relatedMonthlyGoal()).isNull();
					assertThat(taskDto.relatedWeeklyGoal()).isNull();
				});
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 id의 할 일을 조회하여 DTO를 반환한다. (반복 있음)")
	void searchByIdAndCalendarIdWithRepeat() {
		Long memberId = 1L;
		Long calendarId = 2L;
		TaskGroup taskGroup = spy(new TaskGroup(null, null));
		taskGroup.setRepetitionPattern(TaskRepetitionPatternFixture.DAILY.get(taskGroup));
		Long taskId = 4L;
		Task task = spy(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		given(task.getId()).willReturn(taskId);

		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.of(task));

		TaskDetailsResponse taskDto =
				taskQueryService.searchByIdAndCalendarId(taskId, calendarId, memberId);

		assertAll(
				() -> {
					assertThat(taskDto.taskId()).isEqualTo(task.getId());
					assertThat(taskDto.title()).isEqualTo(task.getTitle());
					assertThat(taskDto.description()).isEqualTo(task.getDescription());
					assertThat(taskDto.date()).isEqualTo(task.getDate());
					assertThat(taskDto.colorHex()).isEqualTo(task.getColorHex());
					assertThat(taskDto.isRepeated()).isTrue();
					assertThat(taskDto.repetition()).isNotNull();
					assertThat(taskDto.relatedMonthlyGoal()).isNull();
					assertThat(taskDto.relatedWeeklyGoal()).isNull();
					assertThat(taskGroup.getRepetitionPattern()).isNotEmpty();
					TaskRepetitionPattern repetitionPattern =
							taskGroup.getRepetitionPattern().get();
					assertThat(taskDto.repetition().repetitionType())
							.isEqualTo(repetitionPattern.getRepetitionType());
					assertThat(taskDto.repetition().repetitionPeriod())
							.isEqualTo(repetitionPattern.getRepetitionPeriod());
					assertThat(taskDto.repetition().repetitionStartDate())
							.isEqualTo(repetitionPattern.getRepetitionStartDate());
					assertThat(taskDto.repetition().repetitionEndDate())
							.isEqualTo(repetitionPattern.getRepetitionEndDate());
				});
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 id의 할 일을 조회하여 DTO를 반환한다. (연관목표 있음)")
	void searchByIdAndCalendarIdWithRelatedGoals() {
		Long memberId = 1L;
		Long calendarId = 2L;
		MonthlyGoal monthlyGoal = mock(MonthlyGoal.class);
		TaskGroup taskGroup = new TaskGroup(monthlyGoal, null);
		Long taskId = 4L;
		Task task = spy(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		given(task.getId()).willReturn(taskId);

		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(taskRepository.findByIdAndCalendarId(eq(taskId), eq(calendarId)))
				.willReturn(Optional.of(task));

		TaskDetailsResponse taskDto =
				taskQueryService.searchByIdAndCalendarId(taskId, calendarId, memberId);

		assertAll(
				() -> {
					assertThat(taskDto.taskId()).isEqualTo(task.getId());
					assertThat(taskDto.title()).isEqualTo(task.getTitle());
					assertThat(taskDto.description()).isEqualTo(task.getDescription());
					assertThat(taskDto.date()).isEqualTo(task.getDate());
					assertThat(taskDto.colorHex()).isEqualTo(task.getColorHex());
					assertThat(taskDto.isRepeated()).isFalse();
					assertThat(taskDto.repetition()).isNull();
					assertThat(taskDto.relatedMonthlyGoal()).isNotNull();
					assertThat(taskDto.relatedMonthlyGoal().id()).isEqualTo(monthlyGoal.getId());
					assertThat(taskDto.relatedMonthlyGoal().title())
							.isEqualTo(monthlyGoal.getTitle());
					assertThat(taskDto.relatedWeeklyGoal()).isNull();
				});
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 id의 할 일이 존재하지 않는다면 RecordNotFoundException이 발생한다.")
	void throwRecordNotFoundExceptionIfTaskNotExists() {
		Long memberId = 1L;
		Long calendarId = 2L;
		Long taskId = 3L;

		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(taskRepository.findByIdAndCalendarId(anyLong(), anyLong()))
				.willThrow(new RecordNotFoundException(ErrorCode.TASK_NOT_FOUND));

		assertThatCode(() -> taskQueryService.searchByIdAndCalendarId(taskId, calendarId, memberId))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.TASK_NOT_FOUND.getDescription());
	}
}
