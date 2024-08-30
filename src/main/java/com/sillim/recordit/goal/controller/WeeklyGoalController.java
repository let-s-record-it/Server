package com.sillim.recordit.goal.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.dto.response.WeeklyGoalDetailsResponse;
import com.sillim.recordit.goal.dto.response.WeeklyGoalListResponse;
import com.sillim.recordit.goal.service.WeeklyGoalQueryService;
import com.sillim.recordit.goal.service.WeeklyGoalUpdateService;
import com.sillim.recordit.member.domain.Member;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals/weeks")
public class WeeklyGoalController {

	private static final int FIRST_WEEK = 1;

	private final WeeklyGoalUpdateService weeklyGoalUpdateService;
	private final WeeklyGoalQueryService weeklyGoalQueryService;

	@PostMapping
	public ResponseEntity<Void> addWeeklyGoal(
			@RequestBody @Validated final WeeklyGoalUpdateRequest request,
			@CurrentMember final Member member) {

		Long weeklyGoalId = weeklyGoalUpdateService.addWeeklyGoal(request, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/goals/weeks/" + weeklyGoalId)).build();
	}

	@GetMapping
	public ResponseEntity<List<WeeklyGoalListResponse>> getWeeklyGoalList(
			@RequestParam final Integer year,
			@RequestParam final Integer month,
			@CurrentMember final Member member) {

		List<WeeklyGoal> weeklyGoals =
				weeklyGoalQueryService.searchAllWeeklyGoalByDate(year, month, member.getId());

		Map<Integer, List<WeeklyGoal>> goalsByWeek =
				weeklyGoals.stream()
						.collect(
								Collectors.groupingBy(
										weeklyGoal ->
												changeWeekIfMonthOfStartDateIsNotEqual(
														weeklyGoal, month)));

		return ResponseEntity.ok(
				goalsByWeek.entrySet().stream()
						.map(e -> WeeklyGoalListResponse.of(e.getKey(), e.getValue()))
						.toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<WeeklyGoalDetailsResponse> getWeeklyGoalDetails(
			@PathVariable final Long id, @CurrentMember final Member member) {

		return ResponseEntity.ok(
				WeeklyGoalDetailsResponse.from(
						weeklyGoalQueryService.searchByIdAndCheckAuthority(id, member.getId())));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> modifyWeeklyGoal(
			@Validated @RequestBody final WeeklyGoalUpdateRequest request,
			@PathVariable final Long id,
			@CurrentMember final Member member) {

		weeklyGoalUpdateService.modifyWeeklyGoal(request, id, member.getId());
		return ResponseEntity.noContent().build();
	}

	private Integer changeWeekIfMonthOfStartDateIsNotEqual(
			final WeeklyGoal weeklyGoal, final Integer currentMonth) {
		if (weeklyGoal.getStartDate().getMonthValue() == currentMonth) {
			return weeklyGoal.getWeek();
		}
		return FIRST_WEEK;
	}
}
