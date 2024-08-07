package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.dto.request.CalendarAddRequest;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.calendar.InvalidCalendarException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarService {

	private final CalendarRepository calendarRepository;
	private final MemberQueryService memberQueryService;

	public Calendar searchByCalendarId(Long calendarId) {
		return calendarRepository
				.findById(calendarId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.CALENDAR_NOT_FOUND));
	}

	public List<Calendar> searchByMemberId(Long memberId) {
		return calendarRepository.findByMemberId(memberId);
	}

	@Transactional
	public Calendar addCalendar(CalendarAddRequest request, Long memberId) {
		return calendarRepository.save(
				request.toCalendar(memberQueryService.findByMemberId(memberId)));
	}

	@Transactional
	public void deleteByCalendarId(Long calendarId, Long memberId) {
		Calendar calendar = searchByCalendarId(calendarId);

		if (!calendar.isOwnedBy(memberId)) {
			throw new InvalidCalendarException(ErrorCode.INVALID_CALENDAR_DELETE_REQUEST);
		}
		calendarRepository.delete(calendar);
	}

	public Calendar searchByCalendarId(final Long calendarId, final Long memberId) {
		Calendar calendar = searchByCalendarId(calendarId);
		if (!calendar.isOwnedBy(memberId)) {
			throw new InvalidCalendarException(ErrorCode.CALENDAR_ACCESS_DENIED);
		}
		return calendar;
	}
}
