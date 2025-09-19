package com.sillim.recordit.invite.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.dto.response.CalendarMemberResponse;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.domain.InviteLog;
import com.sillim.recordit.invite.domain.InviteState;
import com.sillim.recordit.invite.repository.InviteLinkRepository;
import com.sillim.recordit.invite.repository.InviteLogRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.pushalarm.dto.PushMessage;
import com.sillim.recordit.pushalarm.service.AlarmService;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {

	private final InviteLinkRepository inviteLinkRepository;
	private final CalendarQueryService calendarQueryService;
	private final InviteLogRepository inviteLogRepository;
	private final AlarmService alarmService;
	private final CalendarMemberService calendarMemberService;
	private final MemberQueryService memberQueryService;

	public String getOrGenerateInviteLink(Long calendarId) {
		Optional<InviteLink> inviteLink =
				inviteLinkRepository.findByCalendarIdAndExpiredIsFalse(calendarId);
		if (isValidInviteLink(inviteLink)) {
			return Base64.getUrlEncoder()
					.encodeToString(inviteLink.get().getInviteCode().getBytes());
		}

		return Base64.getUrlEncoder()
				.encodeToString(
						inviteLinkRepository
								.save(
										new InviteLink(
												UUID.randomUUID().toString(),
												LocalDateTime.now().plusDays(7),
												false,
												calendarQueryService.searchByCalendarId(
														calendarId)))
								.getInviteCode()
								.getBytes());
	}

	public boolean isValidInviteLink(Optional<InviteLink> inviteLink) {
		if (inviteLink.isEmpty()) {
			return false;
		}

		if (LocalDateTime.now().isAfter(inviteLink.get().getExpiredTime())) {
			inviteLink.get().expire();
			return false;
		}

		return true;
	}

	@Transactional(readOnly = true)
	public InviteLink searchInviteInfo(String inviteCode) {
		return inviteLinkRepository.findInfoByInviteCode(
				new String(Base64.getUrlDecoder().decode(inviteCode)));
	}

	@Transactional(readOnly = true)
	public List<Member> searchFollowingsNotInvited(Long calendarId, String personalId) {
		List<CalendarMemberResponse> calendarMembers =
				calendarMemberService.searchCalendarMembers(calendarId);
		return memberQueryService.searchFollowings(personalId).stream()
				.filter(
						follow -> {
							for (var calendarMember : calendarMembers) {
								if (calendarMember.memberId().equals(follow.getId())) {
									return false;
								}
							}
							return true;
						})
				.toList();
	}

	public void inviteMember(Long calendarId, Long invitedMemberId, Member inviter) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		calendar.validateAuthenticatedMember(inviter.getId());

		InviteLog inviteLog =
				inviteLogRepository.save(
						new InviteLog(
								inviter.getId(), invitedMemberId, calendarId, InviteState.WAIT));
		alarmService.pushAlarm(
				inviter.getId(),
				invitedMemberId,
				PushMessage.fromInvite(
						inviteLog.getId(), calendar.getTitle(), inviter.getPersonalId()));
	}

	public void acceptInvite(Long inviteLogId, Long alarmLogId, Long memberId) {
		InviteLog inviteLog =
				inviteLogRepository
						.findById(inviteLogId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.INVITE_LOG_NOT_FOUND));

		if (!inviteLog.isInvitedMember(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}

		calendarMemberService.addCalendarMember(inviteLog.getCalendarId(), memberId);
		inviteLog.accept();
		alarmService.deleteAlarm(alarmLogId);
	}

	public void rejectInvite(Long inviteLogId, Long alarmLogId, Long memberId) {
		InviteLog inviteLog =
				inviteLogRepository
						.findById(inviteLogId)
						.orElseThrow(
								() -> new RecordNotFoundException(ErrorCode.INVITE_LOG_NOT_FOUND));

		if (!inviteLog.isInvitedMember(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}

		inviteLog.reject();
		alarmService.deleteAlarm(alarmLogId);
	}
}
