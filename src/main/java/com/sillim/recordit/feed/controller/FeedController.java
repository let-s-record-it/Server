package com.sillim.recordit.feed.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.dto.response.FeedDetailsResponse;
import com.sillim.recordit.feed.service.FeedCommandService;
import com.sillim.recordit.feed.service.FeedQueryService;
import com.sillim.recordit.member.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

	private final FeedCommandService feedCommandService;
	private final FeedQueryService feedQueryService;

	@PostMapping
	public ResponseEntity<Void> feedAdd(
			@Validated @RequestPart FeedAddRequest feedAddRequest,
			@Valid @Size(max = 10) @RequestPart("images") List<MultipartFile> images,
			@CurrentMember Member member)
			throws IOException {
		Long feedId = feedCommandService.addFeed(feedAddRequest, images, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/feeds/" + feedId)).build();
	}

	@GetMapping("/{feedId}")
	public ResponseEntity<FeedDetailsResponse> feedDetails(
			@PathVariable Long feedId, @CurrentMember Member member) {
		return ResponseEntity.ok(
				FeedDetailsResponse.of(feedQueryService.searchById(feedId), member.getId()));
	}
}
