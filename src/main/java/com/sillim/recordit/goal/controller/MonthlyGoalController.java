package com.sillim.recordit.goal.controller;

import com.sillim.recordit.goal.controller.dto.request.MonthlyGoalAddRequest;
import com.sillim.recordit.goal.service.MonthlyGoalUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goals")
public class MonthlyGoalController {

	private final MonthlyGoalUpdateService monthlyGoalUpdateService;

	// TODO: Security 적용 시 UserDetails 받도록 변경
	@PostMapping("/months")
	public ResponseEntity<Void> monthlyGoalAdd(@Valid @RequestBody MonthlyGoalAddRequest request) {

		monthlyGoalUpdateService.add(request, 1L);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
