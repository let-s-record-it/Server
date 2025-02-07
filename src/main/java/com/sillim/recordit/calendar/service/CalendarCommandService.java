package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarModifyRequest;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.calendar.InvalidCalendarException;
import com.sillim.recordit.member.service.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CalendarCommandService {

	private final CalendarRepository calendarRepository;
	private final MemberQueryService memberQueryService;
	private final CalendarQueryService calendarQueryService;

	public Calendar addCalendar(CalendarAddRequest request, Long memberId) {
		return calendarRepository.save(
				request.toCalendar(memberQueryService.findByMemberId(memberId)));
	}

	public void modifyCalendar(CalendarModifyRequest request, Long calendarId, Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);

		if (!calendar.isOwnedBy(memberId)) {
			throw new InvalidCalendarException(ErrorCode.INVALID_CALENDAR_DELETE_REQUEST);
		}
		calendar.modify(request.title(), request.colorHex());
	}

	public void deleteByCalendarId(Long calendarId, Long memberId) {
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);

		if (!calendar.isOwnedBy(memberId)) {
			throw new InvalidCalendarException(ErrorCode.INVALID_CALENDAR_DELETE_REQUEST);
		}
		calendarRepository.delete(calendar);
	}
}
