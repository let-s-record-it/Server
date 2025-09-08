package com.sillim.recordit.schedule.service;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.request.RepetitionUpdateRequest;
import com.sillim.recordit.schedule.repository.RepetitionPatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RepetitionPatternService {

	private final RepetitionPatternRepository repetitionPatternRepository;

	public RepetitionPattern addRepetitionPattern(RepetitionUpdateRequest request, ScheduleGroup scheduleGroup) {
		return repetitionPatternRepository.save(RepetitionPattern.create(request.repetitionType(),
				request.repetitionPeriod(), request.repetitionStartDate(), request.repetitionEndDate(),
				request.monthOfYear(), request.dayOfMonth(), request.weekNumber(), request.weekday(),
				request.weekdayBit(), scheduleGroup));
	}

	@Transactional(readOnly = true)
	public RepetitionPattern searchByScheduleGroupId(Long scheduleGroupId) {
		return repetitionPatternRepository.findByScheduleGroupId(scheduleGroupId)
				.orElseThrow(() -> new RecordNotFoundException(ErrorCode.REPETITION_PATTERN_NOT_FOUND));
	}

	public RepetitionPattern updateRepetitionPattern(RepetitionUpdateRequest request, ScheduleGroup scheduleGroup) {
		if (scheduleGroup.isRepeated() && scheduleGroup.getRepetitionPattern().isPresent()) {
			scheduleGroup.modifyRepeated(request.repetitionType(), request.repetitionPeriod(),
					request.repetitionStartDate(), request.repetitionEndDate(), request.monthOfYear(),
					request.dayOfMonth(), request.weekNumber(), request.weekday(), request.weekdayBit());
			return scheduleGroup.getRepetitionPattern().get();
		}
		return addRepetitionPattern(request, scheduleGroup);
	}
}
