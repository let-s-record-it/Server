package com.sillim.recordit.member.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.feed.dto.response.FeedCommentInListResponse;
import com.sillim.recordit.feed.service.FeedCommentQueryService;
import com.sillim.recordit.global.dto.response.SliceResponse;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.ProfileModifyRequest;
import com.sillim.recordit.member.dto.response.ProfileResponse;
import com.sillim.recordit.member.service.MemberCommandService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberCommandService memberCommandService;
	private final FeedCommentQueryService feedCommentQueryService;

	@GetMapping("/me")
	public ResponseEntity<ProfileResponse> profileDetails(@CurrentMember Member member) {
		return ResponseEntity.ok(ProfileResponse.of(member));
	}

	@GetMapping("/me/comments")
	public ResponseEntity<SliceResponse<FeedCommentInListResponse>> myCommentList(
			Pageable pageable, @CurrentMember Member member) {
		return ResponseEntity.ok(
				feedCommentQueryService.searchByMemberIdOldCreated(pageable, member.getId()));
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
}
