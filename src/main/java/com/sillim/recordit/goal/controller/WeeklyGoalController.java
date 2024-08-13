package com.sillim.recordit.goal.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.goal.dto.request.WeeklyGoalUpdateRequest;
import com.sillim.recordit.goal.service.WeeklyGoalUpdateService;
import com.sillim.recordit.member.domain.Member;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals/weeks")
public class WeeklyGoalController {

	private final WeeklyGoalUpdateService weeklyGoalUpdateService;

	@PostMapping
	public ResponseEntity<Void> addWeeklyGoal(
			@RequestBody @Validated WeeklyGoalUpdateRequest request, @CurrentMember Member member) {

		Long weeklyGoalId = weeklyGoalUpdateService.addWeeklyGoal(request, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/goals/weeks/" + weeklyGoalId)).build();
	}
}
