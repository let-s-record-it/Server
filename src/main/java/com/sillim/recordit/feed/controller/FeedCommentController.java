package com.sillim.recordit.feed.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.feed.dto.request.FeedCommentAddRequest;
import com.sillim.recordit.feed.service.FeedCommentCommandService;
import com.sillim.recordit.member.domain.Member;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feeds/{feedId}/comments")
@RequiredArgsConstructor
public class FeedCommentController {

	private final FeedCommentCommandService feedCommentCommandService;

	@PostMapping
	public ResponseEntity<Void> feedCommentAdd(
			@RequestBody FeedCommentAddRequest request,
			@PathVariable Long feedId,
			@CurrentMember Member member) {
		Long feedCommentId =
				feedCommentCommandService.addFeedComment(request, feedId, member.getId());
		return ResponseEntity.created(
						URI.create("/api/v1/feeds/" + feedId + "/comments/" + feedCommentId))
				.build();
	}
}
