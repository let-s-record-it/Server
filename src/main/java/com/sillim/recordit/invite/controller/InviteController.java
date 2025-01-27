package com.sillim.recordit.invite.controller;

import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.invite.dto.response.InviteLinkResponse;
import com.sillim.recordit.invite.service.InviteService;
import com.sillim.recordit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invite")
@RequiredArgsConstructor
public class InviteController {

	private final InviteService inviteService;

	@GetMapping("/{calenderId}")
	public ResponseEntity<InviteLinkResponse> getInviteLink(@PathVariable Long calenderId) {
		return ResponseEntity.ok(new InviteLinkResponse(inviteService.generateInviteLink(calenderId)));
	}
}
