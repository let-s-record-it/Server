package com.sillim.recordit.task.mock;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;

public class MockTaskRepetitionPattern extends TaskRepetitionPattern {

	public MockTaskRepetitionPattern(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskGroup taskGroup) {
		super(
				TaskRepetitionType.DAILY,
				repetitionPeriod,
				repetitionStartDate,
				repetitionEndDate,
				null,
				null,
				null,
				null,
				null,
				taskGroup);
	}

	@Override
	public Stream<TemporalAmount> repeatingStream() {
		return Stream.empty();
	}
}
