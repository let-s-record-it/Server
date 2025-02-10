package com.sillim.recordit.calendar.controller;

import com.sillim.recordit.calendar.dto.request.CalendarCategoryAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarCategoryModifyRequest;
import com.sillim.recordit.calendar.dto.response.CalendarCategoryListResponse;
import com.sillim.recordit.calendar.service.CalendarCategoryService;
import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendar-categories")
@RequiredArgsConstructor
public class CalendarCategoryController {

	private final CalendarCategoryService calendarCategoryService;

	@GetMapping
	public ResponseEntity<List<CalendarCategoryListResponse>> calendarCategoryList(
			@CurrentMember Member member) {
		return ResponseEntity.ok(
				calendarCategoryService.searchCalendarCategories(member.getId()).stream()
						.map(CalendarCategoryListResponse::of)
						.toList());
	}

	@PostMapping
	public ResponseEntity<Void> addCalendarCategory(
			@Valid @RequestBody CalendarCategoryAddRequest request, @CurrentMember Member member) {
		Long id = calendarCategoryService.addCategory(request, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/calendar-categories/" + id)).build();
	}

	@PutMapping("/{categoryId}")
	public ResponseEntity<Void> modifyCalendarCategory(
			@PathVariable Long categoryId,
			@Valid @RequestBody CalendarCategoryModifyRequest request,
			@CurrentMember Member member) {
		calendarCategoryService.modifyCategory(request, categoryId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCalendarCategory(
			@PathVariable Long categoryId, @CurrentMember Member member) {
		calendarCategoryService.deleteCategory(categoryId, member.getId());
		return ResponseEntity.noContent().build();
	}
}
