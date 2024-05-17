package com.sillim.recordit.calendar.controller;

import com.sillim.recordit.calendar.dto.response.CalendarResponse;
import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@DeleteMapping("/{calendarId}")
	public ResponseEntity<Void> calendarDelete(
			@PathVariable Long calendarId, @CurrentMember Member member) {
		calendarService.deleteByCalendarId(calendarId, member.getId());
		return ResponseEntity.noContent().build();
	}
}
