package com.sillim.recordit.goal.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.repository.MonthlyGoalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyGoalQueryService {

	private final MonthlyGoalJpaRepository monthlyGoalJpaRepository;

	public MonthlyGoal search(final Long monthlyGoalId) {

		return monthlyGoalJpaRepository
				.findById(monthlyGoalId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.MONTHLY_GOAL_NOT_FOUND));
	}
}
