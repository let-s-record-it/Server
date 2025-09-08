package com.sillim.recordit.feed.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.feed.dto.request.FeedCommentAddRequest;
import com.sillim.recordit.feed.dto.response.FeedCommentInListResponse;
import com.sillim.recordit.feed.service.FeedCommentCommandService;
import com.sillim.recordit.feed.service.FeedCommentQueryService;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.member.domain.Member;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
	private final FeedCommentQueryService feedCommentQueryService;

	@PostMapping
	public ResponseEntity<Void> feedCommentAdd(@RequestBody FeedCommentAddRequest request, @PathVariable Long feedId,
			@CurrentMember Member member) {
		Long feedCommentId = feedCommentCommandService.addFeedComment(request, feedId, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/feeds/" + feedId + "/comments/" + feedCommentId)).build();
	}

	@GetMapping("/{commentId}")
	public ResponseEntity<FeedCommentInListResponse> feedComment(@PathVariable Long commentId,
			@CurrentMember Member member) {
		return ResponseEntity.ok(feedCommentQueryService.searchFeedCommentById(commentId, member.getId()));
	}

	@GetMapping
	public ResponseEntity<SliceResponse<FeedCommentInListResponse>> feedCommentList(@PathVariable Long feedId,
			Pageable pageable, @CurrentMember Member member) {
		return ResponseEntity.ok(feedCommentQueryService.searchPaginatedOldCreated(pageable, feedId, member.getId()));
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> feedRemove(@PathVariable Long commentId, @CurrentMember Member member) {
		feedCommentCommandService.removeFeedComment(commentId, member.getId());
		return ResponseEntity.noContent().build();
	}
}
