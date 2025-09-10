package com.sillim.recordit.calendar.dto.response;

import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.member.domain.Member;

public record CalendarMemberResponse(
		Long id, Long memberId, String memberName, String memberProfileImageUrl) {

	public static CalendarMemberResponse of(CalendarMember calendarMember, Member member) {
		return new CalendarMemberResponse(
				calendarMember.getId(),
				member.getId(),
				member.getName(),
				member.getProfileImageUrl());
	}
}
