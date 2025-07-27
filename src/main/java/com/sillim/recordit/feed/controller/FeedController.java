package com.sillim.recordit.feed.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.feed.dto.request.FeedAddRequest;
import com.sillim.recordit.feed.dto.request.FeedModifyRequest;
import com.sillim.recordit.feed.dto.response.FeedDetailsResponse;
import com.sillim.recordit.feed.dto.response.FeedInListResponse;
import com.sillim.recordit.feed.service.FeedCommandService;
import com.sillim.recordit.feed.service.FeedLikeService;
import com.sillim.recordit.feed.service.FeedQueryService;
import com.sillim.recordit.feed.service.FeedScrapService;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.global.lock.RedisLockUtil;
import com.sillim.recordit.member.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
	private final FeedLikeService feedLikeService;
	private final FeedScrapService feedScrapService;

	@PostMapping
	public ResponseEntity<Void> feedAdd(
			@Validated @RequestPart FeedAddRequest feedAddRequest,
			@Valid @Size(max = 10) @RequestPart(required = false) List<MultipartFile> images,
			@CurrentMember Member member)
			throws IOException {
		Long feedId = feedCommandService.addFeed(feedAddRequest, images, member.getId());
		return ResponseEntity.created(URI.create("/api/v1/feeds/" + feedId)).build();
	}

	@GetMapping("/{feedId}")
	public ResponseEntity<FeedDetailsResponse> feedDetails(
			@PathVariable Long feedId, @CurrentMember Member member) {
		return ResponseEntity.ok(feedQueryService.searchById(feedId, member.getId()));
	}

	@GetMapping
	public ResponseEntity<SliceResponse<FeedInListResponse>> feedList(
			Pageable pageable, @CurrentMember Member member) {
		return ResponseEntity.ok(feedQueryService.searchRecentCreated(pageable, member.getId()));
	}

	@GetMapping("/my-feed")
	public ResponseEntity<SliceResponse<FeedInListResponse>> myFeedList(
			Pageable pageable, @CurrentMember Member member) {
		return ResponseEntity.ok(
				feedQueryService.searchRecentCreatedByMemberId(pageable, member.getId()));
	}

	@PutMapping("/{feedId}")
	public ResponseEntity<Void> feedModify(
			@PathVariable Long feedId,
			@Validated @RequestPart FeedModifyRequest feedModifyRequest,
			@Valid @Size(max = 10) @RequestPart(required = false) List<MultipartFile> newImages,
			@CurrentMember Member member)
			throws IOException {
		feedCommandService.modifyFeed(feedModifyRequest, newImages, feedId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{feedId}")
	public ResponseEntity<Void> feedRemove(@PathVariable Long feedId) {
		feedCommandService.removeFeed(feedId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{feedId}/like")
	public ResponseEntity<Void> feedLike(@PathVariable Long feedId, @CurrentMember Member member) {
		RedisLockUtil.acquireAndRunLock(
				feedId + ":" + member.getId(),
				() -> {
					feedLikeService.feedLike(feedId, member.getId());
					return true;
				});
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{feedId}/unlike")
	public ResponseEntity<Void> feedUnlike(
			@PathVariable Long feedId, @CurrentMember Member member) {
		RedisLockUtil.acquireAndRunLock(
				feedId + ":" + member.getId(),
				() -> {
					feedLikeService.feedUnlike(feedId, member.getId());
					return true;
				});
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{feedId}/scrap")
	public ResponseEntity<Void> feedScrap(@PathVariable Long feedId, @CurrentMember Member member) {
		feedScrapService.feedScrap(feedId, member.getId());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{feedId}/unscrap")
	public ResponseEntity<Void> feedUnScrap(
			@PathVariable Long feedId, @CurrentMember Member member) {
		feedScrapService.feedUnScrap(feedId, member.getId());
		return ResponseEntity.noContent().build();
	}
}
