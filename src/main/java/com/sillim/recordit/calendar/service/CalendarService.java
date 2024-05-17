package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.repository.CalendarRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarService {

	private final CalendarRepository calendarRepository;

	public Calendar searchByCalendarId(Long calendarId) {
		return calendarRepository
				.findById(calendarId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.CALENDAR_NOT_FOUND));
	}

	public List<Calendar> searchByMemberId(Long memberId) {
		return calendarRepository.findByMemberId(memberId);
	}
}
