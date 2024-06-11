package com.sillim.recordit.task.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.task.dto.request.TaskAddRequest;
import com.sillim.recordit.task.dto.response.TaskResponse;
import com.sillim.recordit.task.service.TaskCommandService;
import com.sillim.recordit.task.service.TaskQueryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calendars/{calendarId}/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskCommandService taskCommandService;
	private final TaskQueryService taskQueryService;

	@PostMapping
	public ResponseEntity<Void> addTasks(
			@Validated @RequestBody TaskAddRequest request,
			@PathVariable Long calendarId,
			@CurrentMember Member member) {

		taskCommandService.addTasks(request, calendarId, member.getId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<List<TaskResponse>> getTaskList(
			@PathVariable Long calendarId,
			@RequestParam LocalDate date,
			@CurrentMember Member member) {

		return ResponseEntity.ok(
				taskQueryService.searchAllByDate(calendarId, date, member.getId()).stream()
						.map(TaskResponse::from)
						.toList());
	}
}
