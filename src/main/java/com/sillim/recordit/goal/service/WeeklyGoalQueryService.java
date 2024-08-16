package com.sillim.recordit.goal.service;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.response.WeeklyGoalListResponse;
import com.sillim.recordit.goal.repository.WeeklyGoalRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyGoalQueryService {

	private final WeeklyGoalRepository weeklyGoalRepository;

	public List<WeeklyGoalListResponse> searchAllWeeklyGoalByDate(
			final Integer year, final Integer month, final Long memberId) {

		List<WeeklyGoal> weeklyGoals =
				weeklyGoalRepository.findWeeklyGoalInMonth(year, month, memberId);

		Map<Integer, List<WeeklyGoal>> goalsByWeek =
				weeklyGoals.stream().collect(Collectors.groupingBy(WeeklyGoal::getWeek));

		return goalsByWeek.entrySet().stream()
				.map(e -> WeeklyGoalListResponse.from(e.getKey(), e.getValue()))
				.toList();
	}
}
