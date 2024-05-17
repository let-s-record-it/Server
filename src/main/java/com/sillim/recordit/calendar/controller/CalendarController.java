package com.sillim.recordit.calendar.controller;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.dto.response.CalendarResponse;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calendars")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarService calendarService;

	@GetMapping
	public ResponseEntity<List<CalendarResponse>> calendarList(@CurrentMember Member member) {
		return ResponseEntity.ok(
				calendarService.searchByMemberId(member.getId()).stream()
						.map(CalendarResponse::from)
						.toList());
	}

	@PostMapping
	public ResponseEntity<CalendarResponse> addCalendar(
			@RequestBody CalendarAddRequest request, @CurrentMember Member member) {
		Calendar calendar = calendarService.addCalendar(request, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/calendars/" + calendar.getId()))
				.body(CalendarResponse.from(calendar));
	}
}
