package com.sillim.recordit.invite.controller;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.config.security.authenticate.CurrentMember;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.dto.response.InviteInfoResponse;
import com.sillim.recordit.invite.dto.response.InviteLinkResponse;
import com.sillim.recordit.invite.service.InviteService;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.dto.response.MemberListResponse;
import com.sillim.recordit.member.service.MemberQueryService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invite")
@RequiredArgsConstructor
public class InviteController {

	private final InviteService inviteService;
	private final MemberQueryService memberQueryService;

	@GetMapping("/{calenderId}")
	public ResponseEntity<InviteLinkResponse> getInviteLink(@PathVariable Long calenderId) {
		return ResponseEntity.ok(
				new InviteLinkResponse(inviteService.getOrGenerateInviteLink(calenderId)));
	}

	@GetMapping("/info/{inviteCode}")
	public ResponseEntity<InviteInfoResponse> getInviteInfo(@PathVariable String inviteCode) {
		InviteLink inviteLink = inviteService.searchInviteInfo(inviteCode);
		Calendar calendar = inviteLink.getCalendar();
		Member member = memberQueryService.findByMemberId(calendar.getMemberId());
		return ResponseEntity.ok(
				new InviteInfoResponse(
						calendar.getId(), calendar.getTitle(), member.getId(), member.getName()));
	}

	@GetMapping("/followings")
	public ResponseEntity<List<MemberListResponse>> myFollowingList(
			@RequestParam Long calendarId, @CurrentMember Member member) {
		return ResponseEntity.ok(
				inviteService
						.searchFollowingsNotInvited(calendarId, member.getPersonalId())
						.stream()
						.map(MemberListResponse::of)
						.toList());
	}

	@PostMapping("/members/{inviteMemberId}")
	public ResponseEntity<Void> inviteMember(
			@PathVariable Long inviteMemberId,
			@RequestParam Long calendarId,
			@CurrentMember Member member)
			throws IOException {
		inviteService.inviteMember(calendarId, inviteMemberId, member);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/accept/{inviteId}")
	public ResponseEntity<Void> acceptInvite(
			@PathVariable Long inviteId,
			@RequestParam Long alarmLogId,
			@CurrentMember Member member) {
		inviteService.acceptInvite(inviteId, alarmLogId, member.getId());

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/reject/{inviteId}")
	public ResponseEntity<Void> rejectInvite(
			@PathVariable Long inviteId,
			@RequestParam Long alarmLogId,
			@CurrentMember Member member) {
		inviteService.rejectInvite(inviteId, alarmLogId, member.getId());

		return ResponseEntity.noContent().build();
	}
}
