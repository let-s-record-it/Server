package com.sillim.recordit.member.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.request.ProfileModifyRequest;
import com.sillim.recordit.member.dto.response.ProfileResponse;
import com.sillim.recordit.member.service.MemberCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberCommandService memberCommandService;

	@GetMapping("/me")
	public ResponseEntity<ProfileResponse> profileDetails(@CurrentMember Member member) {
		return ResponseEntity.ok(ProfileResponse.of(member));
	}

	@PutMapping("/me")
	public ResponseEntity<Void> profileModify(
			@CurrentMember Member member, @RequestBody @Valid ProfileModifyRequest request) {
		memberCommandService.modifyMemberInfo(request, member.getId());

		return ResponseEntity.noContent().build();
	}
}
