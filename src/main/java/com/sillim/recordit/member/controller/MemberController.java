package com.sillim.recordit.member.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.feed.dto.response.FeedCommentInListResponse;
import com.sillim.recordit.feed.service.FeedCommentQueryService;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.ProfileModifyRequest;
import com.sillim.recordit.member.dto.request.TokenUpdateRequest;
import com.sillim.recordit.member.dto.response.FollowRecommendResponse;
import com.sillim.recordit.member.dto.response.MemberListResponse;
import com.sillim.recordit.member.dto.response.ProfileResponse;
import com.sillim.recordit.member.service.*;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberCommandService memberCommandService;
	private final FeedCommentQueryService feedCommentQueryService;
	private final MemberDeviceService memberDeviceService;
	private final MemberFollowService memberFollowService;
	private final MemberQueryService memberQueryService;
	private final MemberRecommender memberRecommender;

	@GetMapping("/me")
	public ResponseEntity<ProfileResponse> myProfileDetails(@CurrentMember Member member) {
		return ResponseEntity.ok(ProfileResponse.of(member, true));
	}

	@GetMapping("/me/comments")
	public ResponseEntity<SliceResponse<FeedCommentInListResponse>> myCommentList(
			Pageable pageable, @CurrentMember Member member) {
		return ResponseEntity.ok(
				feedCommentQueryService.searchByMemberIdOldCreated(pageable, member.getId()));
	}

	@GetMapping("/search")
	public ResponseEntity<List<MemberListResponse>> searchMemberList(
			@RequestParam String personalId) {
		return ResponseEntity.ok(
				memberQueryService.searchByPersonalIdPrefix(personalId).stream()
						.map(MemberListResponse::of)
						.toList());
	}

	@GetMapping("/{memberId}")
	public ResponseEntity<ProfileResponse> profileDetails(
			@PathVariable Long memberId, @CurrentMember Member member) {
		return ResponseEntity.ok(
				memberQueryService.searchProfileByMemberId(memberId, member.getId()));
	}

	@GetMapping("/me/recommends/members")
	public ResponseEntity<List<FollowRecommendResponse>> recommendMemberList(
			@CurrentMember Member member) {
		List<FollowRecommendResponse> body =
				memberRecommender.recommendFollows(member.getPersonalId());
		body.forEach(m -> log.info("result: {}", m.personalId()));
		return ResponseEntity.ok(body);
	}

	@PostMapping("/{memberId}/follow")
	public ResponseEntity<Void> follow(@PathVariable Long memberId, @CurrentMember Member member) {
		memberFollowService.follow(member.getId(), memberId);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{memberId}/unfollow")
	public ResponseEntity<Void> unfollow(
			@PathVariable Long memberId, @CurrentMember Member member) {
		memberFollowService.unfollow(member.getId(), memberId);

		return ResponseEntity.noContent().build();
	}

	@PutMapping("/me")
	public ResponseEntity<Void> profileModify(
			@CurrentMember Member member, @RequestBody @Valid ProfileModifyRequest request) {
		memberCommandService.modifyMemberInfo(request, member.getId());

		return ResponseEntity.noContent().build();
	}

	@PutMapping("/me/image")
	public ResponseEntity<Void> profileImageModify(
			@RequestPart MultipartFile newImage, @CurrentMember Member member) throws IOException {
		memberCommandService.modifyMemberProfileImage(newImage, member.getId());

		return ResponseEntity.noContent().build();
	}

	@PutMapping("/me/fcmToken")
	public ResponseEntity<Void> fcmTokenModify(
			@RequestBody TokenUpdateRequest request, @CurrentMember Member member) {
		memberDeviceService.updateFcmToken(request.deviceId(), request.fcmToken(), member.getId());

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/me/withdraw")
	public ResponseEntity<Void> memberWithdrawal(@CurrentMember Member member) {
		memberCommandService.withdrawMember(member.getId());
		return ResponseEntity.noContent().build();
	}
}
