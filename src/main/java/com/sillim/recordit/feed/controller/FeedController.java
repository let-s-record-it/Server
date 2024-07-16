package com.sillim.recordit.feed.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.service.FeedCommandService;
import com.sillim.recordit.feed.service.FeedImageUploadService;
import com.sillim.recordit.member.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedController {

	private final FeedCommandService feedCommandService;
	private final FeedImageUploadService feedImageUploadService;

	@PostMapping
	public ResponseEntity<Void> feedAdd(
			@Validated @RequestPart FeedAddRequest feedAddRequest,
			@RequestPart @Valid @Size(max = 10) List<MultipartFile> images,
			@CurrentMember Member member)
			throws IOException {
		Long feedId = feedCommandService.addFeed(feedAddRequest, images, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/feeds/" + feedId)).build();
	}
}
