package com.sillim.recordit.pushalarm.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.service.AlarmLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alarm-logs")
@RequiredArgsConstructor
public class AlarmLogController {
	private final AlarmLogService alarmLogService;

	@GetMapping
	public ResponseEntity<SliceResponse<PushMessage>> alarmLogList(
			Pageable pageable, @CurrentMember Member member) {
		return ResponseEntity.ok(alarmLogService.searchRecentCreated(pageable, member.getId()));
	}

	@DeleteMapping("/{alarmLogId}")
	public ResponseEntity<Void> deleteAlarmLog(
			@PathVariable Long alarmLogId, @CurrentMember Member member) {
		alarmLogService.deleteAlarmLog(alarmLogId, member.getId());

		return ResponseEntity.noContent().build();
	}
}
