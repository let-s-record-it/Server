package com.sillim.recordit.goal.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.response.GoalListResponse;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.goal.service.WeeklyGoalQueryService;
import com.sillim.recordit.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/calendars/{calendarId}/goals")
public class GoalController {

	private final MonthlyGoalQueryService monthlyGoalQueryService;
	private final WeeklyGoalQueryService weeklyGoalQueryService;

	@GetMapping
	public ResponseEntity<GoalListResponse> getGoalListByDate(
			@RequestParam final Integer year,
			@RequestParam final Integer month,
			@PathVariable final Long calendarId,
			@CurrentMember final Member member) {

		List<MonthlyGoal> monthlyGoals =
				monthlyGoalQueryService.searchAllByDate(year, month, member.getId(), calendarId);
		List<WeeklyGoal> weeklyGoals =
				weeklyGoalQueryService.searchAllWeeklyGoalByDate(
						year, month, member.getId(), calendarId);
		return ResponseEntity.ok(GoalListResponse.of(monthlyGoals, weeklyGoals));
	}
}
