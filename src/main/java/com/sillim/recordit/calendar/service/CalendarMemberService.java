package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarMember;
import com.sillim.recordit.calendar.dto.response.CalendarMemberResponse;
import com.sillim.recordit.calendar.repository.CalendarMemberRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CalendarMemberService {

	private final CalendarMemberRepository calendarMemberRepository;
	private final CalendarQueryService calendarQueryService;
	private final MemberQueryService memberQueryService;

	@Transactional(readOnly = true)
	public CalendarMember searchCalendarMember(Long calendarId, Long memberId) {
		return calendarMemberRepository.findCalendarMember(calendarId, memberId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.CALENDAR_MEMBER_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public void validateCalendarMember(Long calendarId, Long memberId) {
		if (!calendarMemberRepository.existsByCalendarIdAndMemberId(calendarId, memberId)) {
			throw new RecordNotFoundException(ErrorCode.CALENDAR_MEMBER_NOT_FOUND);
		}
	}

	@Transactional(readOnly = true)
	public List<CalendarMemberResponse> searchCalendarMembers(Long calendarId) {
		return calendarMemberRepository.findCalendarMembers(calendarId).stream()
				.map(calendarMember -> CalendarMemberResponse.of(calendarMember,
						memberQueryService.findByMemberId(calendarMember.getMemberId())))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<Calendar> searchCalendarsByMemberId(Long memberId) {
		return calendarMemberRepository.findCalendarsByMemberId(memberId);
	}

	public Long addCalendarMember(Long calendarId, Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		return calendarMemberRepository.save(new CalendarMember(calendar, memberId)).getId();
	}

	public void removeCalendarMember(Long calendarId, Long memberId, Long ownerId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		validateIsCalendarOwner(ownerId, calendar);
		searchCalendarMember(calendarId, memberId).delete();
	}

	public void removeCalendarMembersInCalendar(Long calendarId, Long ownerId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		validateIsCalendarOwner(ownerId, calendar);
		calendarMemberRepository.deleteCalendarMembersInCalendar(calendarId);
	}

	private void validateIsCalendarOwner(Long ownerId, Calendar calendar) {
		if (!calendar.isOwnedBy(ownerId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_CALENDAR_MEMBER_GET_REQUEST);
		}
	}
}
