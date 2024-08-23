package com.sillim.recordit.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPatternFactory;
import com.sillim.recordit.task.dto.request.TaskGroupUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskRepetitionUpdateRequest;
import com.sillim.recordit.task.fixture.TaskRepetitionPatternFixture;
import com.sillim.recordit.task.repository.TaskGroupRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskGroupServiceTest {

	@InjectMocks TaskGroupService taskGroupService;
	@Mock TaskGroupRepository taskGroupRepository;
	@Mock MonthlyGoalQueryService monthlyGoalQueryService;

	// TODO: WeeklyGoal 기능 구현 후 테스트 추가 필요

	@Test
	@DisplayName("반복이 없는 할 일 그룹을 추가할 수 있다.")
	void addNonRepeatingTaskGroup() {
		Long memberId = 1L;
		TaskGroupUpdateRequest request = new TaskGroupUpdateRequest(null, null);
		TaskGroup expected = new TaskGroup(null, null);
		given(taskGroupRepository.save(any(TaskGroup.class))).willReturn(expected);

		TaskGroup saved = taskGroupService.addNonRepeatingTaskGroup(request, memberId);

		assertThat(saved)
				.usingRecursiveComparison()
				.ignoringFields("id", "createdAt", "modifiedAt")
				.isEqualTo(expected);
		then(taskGroupRepository).should(times(1)).save(any(TaskGroup.class));
	}

	@Test
	@DisplayName("반복이 있는 할 일 그룹을 추가할 수 있다.")
	void addRepeatingTaskGroup() {
		Long memberId = 1L;
		TaskGroupUpdateRequest request = new TaskGroupUpdateRequest(null, null);
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
		TaskGroup expected = new TaskGroup(null, null);
		expected.setRepetitionPattern(
				TaskRepetitionPatternFactory.create(
						repetitionRequest.repetitionType(),
						repetitionRequest.repetitionPeriod(),
						repetitionRequest.repetitionStartDate(),
						repetitionRequest.repetitionEndDate(),
						repetitionRequest.monthOfYear(),
						repetitionRequest.dayOfMonth(),
						repetitionRequest.weekNumber(),
						repetitionRequest.weekday(),
						repetitionRequest.weekdayBit(),
						expected));
		given(taskGroupRepository.save(any(TaskGroup.class))).willReturn(expected);

		TaskGroup saved =
				taskGroupService.addRepeatingTaskGroup(request, repetitionRequest, memberId);

		assertThat(saved)
				.usingRecursiveComparison()
				.ignoringFields("id", "createdAt", "modifiedAt", "repetition")
				.isEqualTo(expected);
		assertAll(
				() -> {
					assertThat(saved.getRepetitionPattern()).isNotEmpty();
					TaskRepetitionPattern repetitionPattern = saved.getRepetitionPattern().get();
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(repetitionRequest.repetitionType());
					assertThat(repetitionPattern.getRepetitionPeriod())
							.isEqualTo(repetitionRequest.repetitionPeriod());
					assertThat(repetitionPattern.getRepetitionStartDate())
							.isEqualTo(repetitionRequest.repetitionStartDate());
					assertThat(repetitionPattern.getRepetitionEndDate())
							.isEqualTo(repetitionRequest.repetitionEndDate());
					assertThat(repetitionPattern.getMonthOfYear()).isEmpty();
					assertThat(repetitionPattern.getDayOfMonth()).isEmpty();
					assertThat(repetitionPattern.getWeekNumber()).isEmpty();
					assertThat(repetitionPattern.getWeekday()).isEmpty();
					assertThat(repetitionPattern.getWeekdayBit()).isEmpty();
				});
		then(taskGroupRepository).should(times(1)).save(any(TaskGroup.class));
	}

	@Test
	@DisplayName("반복이 있는 할 일 그룹을 수정하고 반복을 제거한다.")
	void modifyTaskGroupAndMakeNonRepeatableWithRepeatableTask() {
		Long memberId = 1L;
		Long originTaskGroupId = 2L;
		TaskGroupUpdateRequest request = new TaskGroupUpdateRequest(null, null);
		TaskGroup origin = new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class));
		origin.setRepetitionPattern(TaskRepetitionPatternFixture.DAILY.get(origin));
		given(taskGroupRepository.findById(originTaskGroupId)).willReturn(Optional.of(origin));

		TaskGroup modified =
				taskGroupService.modifyTaskGroupAndMakeNonRepeatable(
						originTaskGroupId, request, memberId);

		assertAll(
				() -> {
					assertThat(modified.getIsRepeated()).isFalse();
					assertThat(modified.getMonthlyGoal()).isEmpty();
					assertThat(modified.getWeeklyGoal()).isEmpty();
					assertThat(modified.getRepetitionPattern()).isEmpty();
				});
	}

	@Test
	@DisplayName("반복이 없는 할 일 그룹을 수정한다.")
	void modifyTaskGroupAndMakeNonRepeatableWithNonRepeatableTask() {
		Long memberId = 1L;
		Long originTaskGroupId = 2L;
		TaskGroupUpdateRequest request = new TaskGroupUpdateRequest(null, null);
		TaskGroup origin = new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class));
		given(taskGroupRepository.findById(originTaskGroupId)).willReturn(Optional.of(origin));

		TaskGroup modified =
				taskGroupService.modifyTaskGroupAndMakeNonRepeatable(
						originTaskGroupId, request, memberId);

		assertAll(
				() -> {
					assertThat(modified.getIsRepeated()).isFalse();
					assertThat(modified.getMonthlyGoal()).isEmpty();
					assertThat(modified.getWeeklyGoal()).isEmpty();
					assertThat(modified.getRepetitionPattern()).isEmpty();
				});
	}

	@Test
	@DisplayName("반복이 있는 할 일의 할 일 그룹을 수정하고 반복을 재생성한다.")
	void modifyTaskGroupAndMakeRepeatableWithRepeatableTask() {
		Long memberId = 1L;
		Long originTaskGroupId = 2L;
		TaskGroupUpdateRequest request = new TaskGroupUpdateRequest(null, null);
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
		TaskGroup origin = new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class));
		TaskRepetitionPattern originRepetitionPattern =
				TaskRepetitionPatternFixture.WEEKLY.get(origin);
		origin.setRepetitionPattern(originRepetitionPattern);
		given(taskGroupRepository.findById(originTaskGroupId)).willReturn(Optional.of(origin));

		TaskGroup modified =
				taskGroupService.modifyTaskGroupAndMakeRepeatable(
						originTaskGroupId, request, repetitionRequest, memberId);

		assertAll(
				() -> {
					assertThat(modified.getIsRepeated()).isTrue();
					assertThat(modified.getMonthlyGoal()).isEmpty();
					assertThat(modified.getWeeklyGoal()).isEmpty();
					assertThat(modified.getRepetitionPattern()).isNotEmpty();
					TaskRepetitionPattern modifiedRepetitionPattern =
							modified.getRepetitionPattern().get();
					assertThat(modifiedRepetitionPattern.getRepetitionType())
							.isEqualTo(repetitionRequest.repetitionType());
					assertThat(modifiedRepetitionPattern.getRepetitionPeriod())
							.isEqualTo(repetitionRequest.repetitionPeriod());
					assertThat(modifiedRepetitionPattern.getRepetitionStartDate())
							.isEqualTo(repetitionRequest.repetitionStartDate());
					assertThat(modifiedRepetitionPattern.getRepetitionEndDate())
							.isEqualTo(repetitionRequest.repetitionEndDate());
					assertThat(modifiedRepetitionPattern.getMonthOfYear()).isEmpty();
					assertThat(modifiedRepetitionPattern.getDayOfMonth()).isEmpty();
					assertThat(modifiedRepetitionPattern.getWeekNumber()).isEmpty();
					assertThat(modifiedRepetitionPattern.getWeekday()).isEmpty();
					assertThat(modifiedRepetitionPattern.getWeekdayBit()).isEmpty();
				});
	}

	@Test
	@DisplayName("반복이 없는 할 일의 그룹을 수정하고 반복을 생성한다.")
	void modifyTaskGroupAndMakeRepeatableWithNonRepeatableTask() {
		Long memberId = 1L;
		Long originTaskGroupId = 2L;
		TaskGroupUpdateRequest request = new TaskGroupUpdateRequest(null, null);
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
		TaskGroup origin = new TaskGroup(mock(MonthlyGoal.class), mock(WeeklyGoal.class));
		given(taskGroupRepository.findById(originTaskGroupId)).willReturn(Optional.of(origin));

		TaskGroup modified =
				taskGroupService.modifyTaskGroupAndMakeRepeatable(
						originTaskGroupId, request, repetitionRequest, memberId);

		assertAll(
				() -> {
					assertThat(modified.getIsRepeated()).isTrue();
					assertThat(modified.getMonthlyGoal()).isEmpty();
					assertThat(modified.getWeeklyGoal()).isEmpty();
					assertThat(modified.getRepetitionPattern()).isNotEmpty();
					TaskRepetitionPattern modifiedRepetitionPattern =
							modified.getRepetitionPattern().get();
					assertThat(modifiedRepetitionPattern.getRepetitionType())
							.isEqualTo(repetitionRequest.repetitionType());
					assertThat(modifiedRepetitionPattern.getRepetitionPeriod())
							.isEqualTo(repetitionRequest.repetitionPeriod());
					assertThat(modifiedRepetitionPattern.getRepetitionStartDate())
							.isEqualTo(repetitionRequest.repetitionStartDate());
					assertThat(modifiedRepetitionPattern.getRepetitionEndDate())
							.isEqualTo(repetitionRequest.repetitionEndDate());
					assertThat(modifiedRepetitionPattern.getMonthOfYear()).isEmpty();
					assertThat(modifiedRepetitionPattern.getDayOfMonth()).isEmpty();
					assertThat(modifiedRepetitionPattern.getWeekNumber()).isEmpty();
					assertThat(modifiedRepetitionPattern.getWeekday()).isEmpty();
					assertThat(modifiedRepetitionPattern.getWeekdayBit()).isEmpty();
				});
	}
}
