package com.sillim.recordit.task.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.repository.TaskGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskGroupService {

	private final MonthlyGoalQueryService monthlyGoalQueryService;
	// TODO: WeeklyGoal 기능 구현 후 추가 필요

	private final TaskGroupRepository taskGroupRepository;

	@Transactional
	public TaskGroup addTaskGroup(
			final Boolean isRepeated,
			final Long relatedMonthlyGoalId,
			final Long relatedWeeklyGoalId,
			final Long memberId) {
		MonthlyGoal monthlyGoal =
				monthlyGoalQueryService
						.searchOptionalById(relatedMonthlyGoalId, memberId)
						.orElse(null);
		WeeklyGoal weeklyGoal = null;
		return taskGroupRepository.save(new TaskGroup(isRepeated, monthlyGoal, weeklyGoal));
	}

	@Transactional
	public TaskGroup modifyTaskGroup(
			final Long taskGroupId,
			final Boolean isRepeated,
			final Long relatedMonthlyGoalId,
			final Long relatedWeeklyGoalId,
			final Long memberId) {
		TaskGroup taskGroup =
				taskGroupRepository
						.findById(taskGroupId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.TASK_GROUP_NOT_FOUND));
		MonthlyGoal monthlyGoal =
				monthlyGoalQueryService
						.searchOptionalById(relatedMonthlyGoalId, memberId)
						.orElse(null);
		WeeklyGoal weeklyGoal = null;
		taskGroup.modify(isRepeated, monthlyGoal, weeklyGoal);
		return taskGroup;
	}
}
