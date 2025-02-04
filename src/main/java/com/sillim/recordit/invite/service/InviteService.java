package com.sillim.recordit.invite.service;

import com.sillim.recordit.calendar.service.CalendarService;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.dto.response.InviteInfoResponse;
import com.sillim.recordit.invite.repository.InviteLinkRepository;
import java.time.LocalDateTime;
import java.util.Base64;
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
	private final CalendarService calendarService;

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
												calendarService.searchByCalendarId(calendarId)))
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
	public InviteInfoResponse searchInviteInfo(String inviteCode) {
		return inviteLinkRepository.findInfoByInviteCode(
				new String(Base64.getUrlDecoder().decode(inviteCode)));
	}
}
