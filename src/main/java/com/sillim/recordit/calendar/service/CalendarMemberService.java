package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.calendar.repository.CalendarMemberRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public void deleteCalendarMember(Long calendarId, Long memberId, Long ownerId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		if (!calendar.isOwnedBy(ownerId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_CALENDAR_MEMBER_GET_REQUEST);
		}
		calendarMemberRepository.deleteByCalendarIdAndMemberId(calendarId, memberId);
	}
}
