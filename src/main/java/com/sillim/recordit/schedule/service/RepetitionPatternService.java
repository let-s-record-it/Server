package com.sillim.recordit.schedule.service;

import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.RepetitionAddRequest;
import com.sillim.recordit.schedule.repository.RepetitionPatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepetitionPatternService {

	private final RepetitionPatternRepository repetitionPatternRepository;

	public RepetitionPattern addRepetitionPattern(
			RepetitionAddRequest request, ScheduleGroup scheduleGroup) {
		return repetitionPatternRepository.save(
				RepetitionPattern.create(
						request.repetitionType(),
						request.repetitionPeriod(),
						request.repetitionStartDate(),
						request.repetitionEndDate(),
						request.monthOfYear(),
						request.dayOfMonth(),
						request.weekNumber(),
						request.weekday(),
						request.weekdayBit(),
						scheduleGroup));
	}
}
