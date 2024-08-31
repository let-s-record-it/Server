package com.sillim.recordit.task.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.RelatedGoals;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.goal.service.WeeklyGoalQueryService;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPatternFactory;
import com.sillim.recordit.task.dto.request.TaskGroupUpdateRequest;
import com.sillim.recordit.task.dto.request.TaskRepetitionUpdateRequest;
import com.sillim.recordit.task.repository.TaskGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskGroupService {

	private final MonthlyGoalQueryService monthlyGoalQueryService;
	private final WeeklyGoalQueryService weeklyGoalQueryService;

	private final TaskGroupRepository taskGroupRepository;

	@Transactional
	public TaskGroup addNonRepeatingTaskGroup(
			final TaskGroupUpdateRequest request, final Long memberId) {
		RelatedGoals relatedGoals =
				getRelatedGoals(
						request.relatedMonthlyGoalId(), request.relatedWeeklyGoalId(), memberId);
		TaskGroup taskGroup = new TaskGroup(relatedGoals.monthlyGoal(), relatedGoals.weeklyGoal());
		return taskGroupRepository.save(taskGroup);
	}

	@Transactional
	public TaskGroup addRepeatingTaskGroup(
			final TaskGroupUpdateRequest request,
			final TaskRepetitionUpdateRequest repetitionRequest,
			final Long memberId) {
		RelatedGoals relatedGoals =
				getRelatedGoals(
						request.relatedMonthlyGoalId(), request.relatedWeeklyGoalId(), memberId);
		TaskGroup taskGroup = new TaskGroup(relatedGoals.monthlyGoal(), relatedGoals.weeklyGoal());

		taskGroup.setRepetitionPattern(
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
						taskGroup));
		return taskGroupRepository.save(taskGroup);
	}

	@Transactional
	public TaskGroup modifyTaskGroup(
			final Long taskGroupId, final TaskGroupUpdateRequest request, final Long memberId) {
		TaskGroup taskGroup =
				taskGroupRepository
						.findById(taskGroupId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.TASK_GROUP_NOT_FOUND));
		RelatedGoals relatedGoals =
				getRelatedGoals(
						request.relatedMonthlyGoalId(), request.relatedWeeklyGoalId(), memberId);
		taskGroup.modify(relatedGoals.monthlyGoal(), relatedGoals.weeklyGoal());

		return taskGroup;
	}

	@Transactional
	public TaskGroup modifyTaskGroupAndMakeNonRepeatable(
			final Long taskGroupId, final TaskGroupUpdateRequest request, final Long memberId) {
		TaskGroup taskGroup =
				taskGroupRepository
						.findById(taskGroupId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.TASK_GROUP_NOT_FOUND));
		RelatedGoals relatedGoals =
				getRelatedGoals(
						request.relatedMonthlyGoalId(), request.relatedWeeklyGoalId(), memberId);
		taskGroup.modify(relatedGoals.monthlyGoal(), relatedGoals.weeklyGoal());

		if (taskGroup.getIsRepeated()) {
			taskGroup.removeRepetitionPattern();
			taskGroupRepository.flush();
		}
		return taskGroup;
	}

	@Transactional
	public TaskGroup modifyTaskGroupAndMakeRepeatable(
			final Long taskGroupId,
			final TaskGroupUpdateRequest request,
			final TaskRepetitionUpdateRequest repetitionRequest,
			final Long memberId) {
		TaskGroup taskGroup =
				taskGroupRepository
						.findById(taskGroupId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.TASK_GROUP_NOT_FOUND));
		RelatedGoals relatedGoals =
				getRelatedGoals(
						request.relatedMonthlyGoalId(), request.relatedWeeklyGoalId(), memberId);
		taskGroup.modify(relatedGoals.monthlyGoal(), relatedGoals.weeklyGoal());

		if (taskGroup.getIsRepeated()) {
			taskGroup.removeRepetitionPattern();
			taskGroupRepository.flush();
		}
		taskGroup.setRepetitionPattern(
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
						taskGroup));
		return taskGroup;
	}

	private RelatedGoals getRelatedGoals(
			final Long monthlyGoalId, final Long weeklyGoalId, final Long memberId) {
		if (monthlyGoalId == null && weeklyGoalId == null) {
			return RelatedGoals.empty();
		}
		if (monthlyGoalId == null) {
			WeeklyGoal weeklyGoal =
					weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId, memberId);
			return RelatedGoals.from(weeklyGoal);
		}
		if (weeklyGoalId == null) {
			MonthlyGoal monthlyGoal =
					monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId, memberId);
			return RelatedGoals.from(monthlyGoal);
		}
		MonthlyGoal monthlyGoal =
				monthlyGoalQueryService.searchByIdAndCheckAuthority(monthlyGoalId, memberId);
		WeeklyGoal weeklyGoal =
				weeklyGoalQueryService.searchByIdAndCheckAuthority(weeklyGoalId, memberId);
		return RelatedGoals.of(monthlyGoal, weeklyGoal);
	}
}
