package com.sillim.recordit.goal.service;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyGoalQueryService {

	private final WeeklyGoalRepository weeklyGoalRepository;

	public List<WeeklyGoal> searchAllWeeklyGoalByDate(
			final Integer year, final Integer month, final Long memberId) {

		return weeklyGoalRepository.findWeeklyGoalInMonth(year, month, memberId);
	}
}
