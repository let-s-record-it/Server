package com.sillim.recordit.pushalarm.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.pushalarm.service.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
public class SSEController {

	private final SseEmitterManager sseEmitterManager;

	@GetMapping("/subscribe")
	public SseEmitter subscribe(@CurrentMember Member member) {
		return sseEmitterManager.subscribe(member.getId());
	}
}
