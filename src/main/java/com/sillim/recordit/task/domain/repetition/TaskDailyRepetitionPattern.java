package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskDailyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskDailyRepetitionPattern(final TaskRepetitionType repetitionType, final Integer repetitionPeriod,
			final LocalDate repetitionStartDate, final LocalDate repetitionEndDate, final TaskGroup taskGroup) {
		super(repetitionType, repetitionPeriod, repetitionStartDate, repetitionEndDate, null, null, null, null, null,
				taskGroup);
	}

	public static TaskRepetitionPattern createDaily(final Integer repetitionPeriod, final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate, final TaskGroup taskGroup) {
		return TaskDailyRepetitionPattern.builder().repetitionType(TaskRepetitionType.DAILY)
				.repetitionPeriod(repetitionPeriod).repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate).taskGroup(taskGroup).build();
	}

	@Override
	public Stream<TemporalAmount> repeatingStream() {
		return Stream
				.iterate(0, day -> getRepetitionStartDate().plusDays(day).isBefore(getRepetitionEndDate().plusDays(1L)),
						day -> day + getRepetitionPeriod())
				.map(Period::ofDays);
	}
}
