package com.sillim.recordit.feed.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.service.FeedCommandService;
import com.sillim.recordit.member.domain.Member;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

	private final FeedCommandService feedCommandService;

	@PostMapping
	public ResponseEntity<Void> FeedAdd(
			@RequestBody FeedAddRequest request, @CurrentMember Member member) {
		Long feedId = feedCommandService.addFeed(request, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/feeds/" + feedId)).build();
	}
}
