package com.sillim.recordit.category.controller;

import com.sillim.recordit.category.dto.request.ScheduleCategoryAddRequest;
import com.sillim.recordit.category.dto.request.ScheduleCategoryModifyRequest;
import com.sillim.recordit.category.dto.response.ScheduleCategoryListResponse;
import com.sillim.recordit.category.service.ScheduleCategoryCommandService;
import com.sillim.recordit.category.service.ScheduleCategoryQueryService;
import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ScheduleCategoryController {

	private final ScheduleCategoryQueryService scheduleCategoryQueryService;
	private final ScheduleCategoryCommandService scheduleCategoryCommandService;

	@GetMapping
	public ResponseEntity<List<ScheduleCategoryListResponse>> calendarCategoryList(
			@CurrentMember Member member) {
		return ResponseEntity.ok(
				scheduleCategoryQueryService.searchScheduleCategories(member.getId()).stream()
						.map(ScheduleCategoryListResponse::of)
						.toList());
	}

	@PostMapping
	public ResponseEntity<Void> addCalendarCategory(
			@Valid @RequestBody ScheduleCategoryAddRequest request, @CurrentMember Member member) {
		Long id = scheduleCategoryCommandService.addCategory(request, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/categories/" + id)).build();
	}

	@PutMapping("/{categoryId}")
	public ResponseEntity<Void> modifyCalendarCategory(
			@PathVariable Long categoryId,
			@Valid @RequestBody ScheduleCategoryModifyRequest request,
			@CurrentMember Member member) {
		scheduleCategoryCommandService.modifyCategory(request, categoryId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCalendarCategory(
			@PathVariable Long categoryId, @CurrentMember Member member) {
		scheduleCategoryCommandService.deleteCategory(categoryId, member.getId());
		return ResponseEntity.noContent().build();
	}
}
