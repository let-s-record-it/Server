package com.sillim.recordit.task.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.goal.InvalidMonthlyGoalException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.repository.TaskGroupRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskGroupService {

	private final TaskGroupRepository taskGroupRepository;
	private final MonthlyGoalRepository monthlyGoalRepository;

	// TODO: WeeklyGoal 기능 구현 후 추가 필요

	@Transactional
	public TaskGroup addTaskGroup(
			final Boolean isRepeated,
			final Long relatedMonthlyGoalId,
			final Long relatedWeeklyGoalId,
			final Long memberId) {
		final Optional<MonthlyGoal> monthlyGoal =
				monthlyGoalRepository.findById(relatedMonthlyGoalId);
		final Optional<WeeklyGoal> weeklyGoal = Optional.empty();
		checkGoalOwner(monthlyGoal, weeklyGoal, memberId);
		return taskGroupRepository.save(
				new TaskGroup(isRepeated, monthlyGoal.orElse(null), weeklyGoal.orElse(null)));
	}

	private void checkGoalOwner(
			final Optional<MonthlyGoal> monthlyGoal,
			final Optional<WeeklyGoal> weeklyGoal,
			final Long memberId) {
		monthlyGoal.ifPresent(
				goal -> {
					if (!goal.isOwnedBy(memberId)) {
						throw new InvalidMonthlyGoalException(ErrorCode.MONTHLY_GOAL_ACCESS_DENIED);
					}
				});
		weeklyGoal.ifPresent(
				goal -> {
					if (!goal.isOwnedBy(memberId)) {
						throw new InvalidMonthlyGoalException(ErrorCode.WEEKLY_GOAL_ACCESS_DENIED);
					}
				});
	}
}
