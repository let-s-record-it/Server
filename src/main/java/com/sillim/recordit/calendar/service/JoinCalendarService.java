package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.invite.domain.InviteLink;
import com.sillim.recordit.invite.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class JoinCalendarService {

	private final CalendarMemberService calendarMemberService;
	private final InviteService inviteService;

	public Long joinInCalendar(String inviteCode, Long joinMemberId) {
		InviteLink inviteLink = inviteService.searchInviteInfo(inviteCode);
		Calendar calendar = inviteLink.getCalendar();
		if (calendar.isDeleted()) {
			throw new InvalidRequestException(ErrorCode.INVALID_CALENDAR_GET_REQUEST);
		}
		return calendarMemberService.addCalendarMember(calendar.getId(), joinMemberId);
	}
}
