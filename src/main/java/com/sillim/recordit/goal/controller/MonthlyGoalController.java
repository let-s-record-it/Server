package com.sillim.recordit.goal.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.global.validation.goal.ValidMonth;
import com.sillim.recordit.global.validation.goal.ValidYear;
import com.sillim.recordit.goal.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.dto.response.MonthlyGoalDetailsResponse;
import com.sillim.recordit.goal.dto.response.MonthlyGoalListResponse;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.goal.service.MonthlyGoalUpdateService;
import com.sillim.recordit.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
public class MonthlyGoalController {

	private final MonthlyGoalUpdateService monthlyGoalUpdateService;
	private final MonthlyGoalQueryService monthlyGoalQueryService;

	@PostMapping("/months")
	public ResponseEntity<Long> monthlyGoalAdd(
			@Validated @RequestBody final MonthlyGoalUpdateRequest request,
			@CurrentMember final Member member) {

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(monthlyGoalUpdateService.add(request, member.getId()));
	}

	@PutMapping("/months/{monthlyGoalId}")
	public ResponseEntity<Void> monthlyGoalModify(
			@Validated @RequestBody final MonthlyGoalUpdateRequest request,
			@PathVariable final Long monthlyGoalId,
			@CurrentMember final Member member) {

		monthlyGoalUpdateService.modify(request, monthlyGoalId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/months")
	public ResponseEntity<List<MonthlyGoalListResponse>> monthlyGoalList(
			@RequestParam @ValidYear final Integer year,
			@RequestParam @ValidMonth final Integer month,
			@CurrentMember final Member member) {
		return ResponseEntity.ok(
				monthlyGoalQueryService.searchAllByDate(year, month, member.getId()).stream()
						.map(MonthlyGoalListResponse::from)
						.toList());
	}

	@GetMapping("/months/{monthlyGoalId}")
	public ResponseEntity<MonthlyGoalDetailsResponse> monthlyGoalDetails(
			@PathVariable final Long monthlyGoalId, @CurrentMember final Member member) {

		return ResponseEntity.ok(
				MonthlyGoalDetailsResponse.from(
						monthlyGoalQueryService.searchById(monthlyGoalId, member.getId())));
	}

	@PatchMapping("/months/{monthlyGoalId}")
	public ResponseEntity<Void> monthlyGoalChangeAchieveStatus(
			@PathVariable final Long monthlyGoalId,
			@RequestParam final Boolean status,
			@CurrentMember final Member member) {

		monthlyGoalUpdateService.changeAchieveStatus(monthlyGoalId, status, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/months/{monthlyGoalId}")
	public ResponseEntity<Void> monthlyGoalRemove(
			@PathVariable final Long monthlyGoalId, @CurrentMember final Member member) {

		monthlyGoalUpdateService.remove(monthlyGoalId, member.getId());
		return ResponseEntity.noContent().build();
	}
}
