package com.sillim.recordit.goal.controller;

import com.sillim.recordit.global.validation.goal.ValidMonth;
import com.sillim.recordit.global.validation.goal.ValidYear;
import com.sillim.recordit.goal.dto.request.MonthlyGoalUpdateRequest;
import com.sillim.recordit.goal.dto.response.MonthlyGoalDetailsResponse;
import com.sillim.recordit.goal.dto.response.MonthlyGoalListResponse;
import com.sillim.recordit.goal.service.MonthlyGoalQueryService;
import com.sillim.recordit.goal.service.MonthlyGoalUpdateService;
import com.sillim.recordit.member.domain.AuthorizedUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/v1/goals")
public class MonthlyGoalController {

	private final MonthlyGoalUpdateService monthlyGoalUpdateService;
	private final MonthlyGoalQueryService monthlyGoalQueryService;

	@PostMapping("/months")
	public ResponseEntity<Void> monthlyGoalAdd(
			@Validated @RequestBody final MonthlyGoalUpdateRequest request,
			@AuthenticationPrincipal final AuthorizedUser authorizedUser) {

		monthlyGoalUpdateService.add(request, authorizedUser.getMemberId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/months/{monthlyGoalId}")
	public ResponseEntity<Void> monthlyGoalModify(
			@Validated @RequestBody final MonthlyGoalUpdateRequest request,
			@PathVariable final Long monthlyGoalId,
			@AuthenticationPrincipal AuthorizedUser authorizedUser) {

		monthlyGoalUpdateService.modify(request, monthlyGoalId, authorizedUser.getMemberId());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/months")
	public ResponseEntity<List<MonthlyGoalListResponse>> monthlyGoalList(
			@RequestParam @ValidYear final Integer year,
			@RequestParam @ValidMonth final Integer month,
			@AuthenticationPrincipal final AuthorizedUser authorizedUser) {
		return ResponseEntity.ok(
				monthlyGoalQueryService
						.searchAllByDate(year, month, authorizedUser.getMemberId())
						.stream()
						.map(MonthlyGoalListResponse::from)
						.toList());
	}

	@GetMapping("/months/{monthlyGoalId}")
	public ResponseEntity<MonthlyGoalDetailsResponse> monthlyGoalDetails(
			@PathVariable final Long monthlyGoalId,
			@AuthenticationPrincipal final AuthorizedUser authorizedUser) {

		return ResponseEntity.ok(
				MonthlyGoalDetailsResponse.from(
						monthlyGoalQueryService.search(
								monthlyGoalId, authorizedUser.getMemberId())));
	}
}
