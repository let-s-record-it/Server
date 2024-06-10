package com.sillim.recordit.task.service;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPatternFactory;
import com.sillim.recordit.task.dto.TaskRepetitionAddRequest;
import com.sillim.recordit.task.repository.TaskRepetitionPatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskRepetitionPatternService {

	private final TaskRepetitionPatternRepository repetitionPatternRepository;

	@Transactional
	public TaskRepetitionPattern addRepetitionPattern(
			TaskRepetitionAddRequest request, TaskGroup taskGroup) {
		return repetitionPatternRepository.save(
				TaskRepetitionPatternFactory.create(
						request.repetitionType(),
						request.repetitionPeriod(),
						request.repetitionStartDate(),
						request.repetitionEndDate(),
						request.monthOfYear(),
						request.dayOfMonth(),
						request.weekNumber(),
						request.weekday(),
						request.weekdayBit(),
						taskGroup));
	}
}
