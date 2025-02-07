package com.sillim.recordit.calendar.controller;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarModifyRequest;
import com.sillim.recordit.calendar.dto.request.JoinInCalendarRequest;
import com.sillim.recordit.calendar.dto.response.CalendarMemberResponse;
import com.sillim.recordit.calendar.dto.response.CalendarResponse;
import com.sillim.recordit.calendar.service.CalendarCommandService;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.calendar.service.JoinCalendarService;
import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendars")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarQueryService calendarQueryService;
	private final CalendarCommandService calendarCommandService;
	private final CalendarMemberService calendarMemberService;
	private final JoinCalendarService joinCalendarService;

	@GetMapping
	public ResponseEntity<List<CalendarResponse>> calendarList(@CurrentMember Member member) {
		return ResponseEntity.ok(
				calendarMemberService.searchCalendarsByMemberId(member.getId()).stream()
						.map(CalendarResponse::from)
						.toList());
	}

	@PostMapping
	public ResponseEntity<CalendarResponse> addCalendar(
			@RequestBody @Valid CalendarAddRequest request, @CurrentMember Member member) {
		Calendar calendar = calendarCommandService.addCalendar(request, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/calendars/" + calendar.getId()))
				.body(CalendarResponse.from(calendar));
	}

	@PutMapping("/{calendarId}")
	public ResponseEntity<Void> calendarModify(
			@RequestBody @Valid CalendarModifyRequest request,
			@PathVariable Long calendarId,
			@CurrentMember Member member) {
		calendarCommandService.modifyCalendar(request, calendarId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{calendarId}")
	public ResponseEntity<Void> calendarDelete(
			@PathVariable Long calendarId, @CurrentMember Member member) {
		calendarCommandService.deleteByCalendarId(calendarId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{calendarId}/members")
	public ResponseEntity<List<CalendarMemberResponse>> calendarMemberList(
			@PathVariable Long calendarId) {
		return ResponseEntity.ok(
				calendarMemberService.searchCalendarMembers(calendarId).stream()
						.map(CalendarMemberResponse::of)
						.toList());
	}

	@GetMapping("/{calendarId}/members/{memberId}")
	public ResponseEntity<CalendarMemberResponse> calendarMemberDetails(
			@PathVariable Long calendarId, @PathVariable Long memberId) {
		return ResponseEntity.ok(
				CalendarMemberResponse.of(
						calendarMemberService.searchCalendarMember(calendarId, memberId)));
	}

	@PostMapping("/join")
	public ResponseEntity<Void> joinInCalendar(
			@RequestBody JoinInCalendarRequest request, @CurrentMember Member member) {
		joinCalendarService.joinInCalendar(request.inviteCode(), member.getId());
		return ResponseEntity.created(URI.create("")).build();
	}
}
