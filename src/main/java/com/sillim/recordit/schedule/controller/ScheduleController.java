package com.sillim.recordit.schedule.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.dto.request.ScheduleModifyRequest;
import com.sillim.recordit.schedule.dto.response.DayScheduleResponse;
import com.sillim.recordit.schedule.dto.response.MonthScheduleResponse;
import com.sillim.recordit.schedule.service.ScheduleCommandService;
import com.sillim.recordit.schedule.service.ScheduleQueryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calendars/{calendarId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

	private final ScheduleCommandService scheduleCommandService;
	private final ScheduleQueryService scheduleQueryService;

	@PostMapping
	public ResponseEntity<List<MonthScheduleResponse>> schedulesAdd(
			@Validated @RequestBody ScheduleAddRequest request, @PathVariable Long calendarId) {
		return ResponseEntity.ok(
				scheduleCommandService.addSchedules(request, calendarId).stream()
						.map(MonthScheduleResponse::from)
						.toList());
	}

	@GetMapping("/{scheduleId}")
	public ResponseEntity<DayScheduleResponse> scheduleDetails(
			@PathVariable Long scheduleId, @CurrentMember Member member) {
		return ResponseEntity.ok(scheduleQueryService.searchSchedule(scheduleId, member.getId()));
	}

	@GetMapping("/month")
	public ResponseEntity<List<MonthScheduleResponse>> schedulesInMonth(
			@PathVariable Long calendarId,
			@RequestParam Integer year,
			@RequestParam Integer month,
			@CurrentMember Member member) {
		return ResponseEntity.ok(
				scheduleQueryService.searchSchedulesInMonth(
						calendarId, year, month, member.getId()));
	}

	@GetMapping("/day")
	public ResponseEntity<List<DayScheduleResponse>> schedulesInDay(
			@PathVariable Long calendarId,
			@RequestParam LocalDate date,
			@CurrentMember Member member) {
		return ResponseEntity.ok(
				scheduleQueryService.searchSchedulesInDay(calendarId, date, member.getId()));
	}

	@PutMapping("/{scheduleId}")
	public ResponseEntity<Void> scheduleModify(
			@PathVariable Long scheduleId,
			@RequestBody ScheduleModifyRequest request,
			@CurrentMember Member member) {
		scheduleCommandService.modifySchedule(request, scheduleId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{scheduleId}/group")
	public ResponseEntity<Void> schedulesModify(
			@PathVariable Long scheduleId,
			@RequestBody ScheduleModifyRequest request,
			@CurrentMember Member member) {
		scheduleCommandService.modifySchedulesInGroup(request, scheduleId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{scheduleId}")
	public ResponseEntity<Void> scheduleRemoveById(
			@PathVariable Long scheduleId, @CurrentMember Member member) {
		scheduleCommandService.removeSchedule(scheduleId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{scheduleId}/group")
	public ResponseEntity<Void> schedulesRemoveInGroup(
			@PathVariable Long scheduleId, @CurrentMember Member member) {
		scheduleCommandService.removeSchedulesInGroup(scheduleId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{scheduleId}/after")
	public ResponseEntity<Void> schedulesRemoveInGroupAfter(
			@PathVariable Long scheduleId, @CurrentMember Member member) {
		scheduleCommandService.removeSchedulesInGroupAfter(scheduleId, member.getId());
		return ResponseEntity.noContent().build();
	}
}
