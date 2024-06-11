package com.sillim.recordit.goal.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.global.exception.goal.InvalidMonthlyGoalException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.repository.MonthlyGoalRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyGoalQueryService {

	private final MonthlyGoalRepository monthlyGoalRepository;

	public MonthlyGoal search(final Long monthlyGoalId, final Long memberId) {

		final MonthlyGoal monthlyGoal =
				monthlyGoalRepository
						.findById(monthlyGoalId)
						.orElseThrow(
								() ->
										new RecordNotFoundException(
												ErrorCode.MONTHLY_GOAL_NOT_FOUND));
		if (!monthlyGoal.isOwnedBy(memberId)) {
			throw new InvalidMonthlyGoalException(ErrorCode.MONTHLY_GOAL_ACCESS_DENIED);
		}
		return monthlyGoal;
	}

	public List<MonthlyGoal> searchAllByDate(
			final Integer year, final Integer month, final Long memberId) {

		return monthlyGoalRepository.findMonthlyGoalInMonth(year, month, memberId);
	}
}
