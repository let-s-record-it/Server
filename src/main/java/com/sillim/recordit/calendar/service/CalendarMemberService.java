package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.calendar.repository.CalendarMemberRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarMemberService {

	private final CalendarMemberRepository calendarMemberRepository;
	private final CalendarQueryService calendarQueryService;
	private final MemberQueryService memberQueryService;

	public CalendarMember searchCalendarMember(Long calendarId, Long memberId) {
		return calendarMemberRepository
				.findCalendarMember(calendarId, memberId)
				.orElseThrow(
						() -> new RecordNotFoundException(ErrorCode.CALENDAR_MEMBER_NOT_FOUND));
	}

	public List<CalendarMember> searchCalendarMembers(Long calendarId) {
		return calendarMemberRepository.findCalendarMembers(calendarId);
	}

	public List<Calendar> searchCalendarsByMemberId(Long memberId) {
		return calendarMemberRepository.findCalendarsByMemberId(memberId);
	}

	@Transactional
	public Long addCalendarMember(Long calendarId, Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		Member member = memberQueryService.findByMemberId(memberId);
		return calendarMemberRepository.save(new CalendarMember(member, calendar)).getId();
	}
}
