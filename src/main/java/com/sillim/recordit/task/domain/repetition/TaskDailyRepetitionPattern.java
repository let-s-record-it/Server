package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.task.domain.TaskGroup;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;

class TaskDailyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskDailyRepetitionPattern(
			RepetitionType repetitionType,
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			TaskGroup taskGroup) {
		this.repetitionType = repetitionType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.taskGroup = taskGroup;
	}

	protected static TaskDailyRepetitionPattern createDaily(
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			TaskGroup taskGroup) {
		return TaskDailyRepetitionPattern.builder()
				.repetitionType(RepetitionType.DAILY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.taskGroup(taskGroup)
				.build();
	}

	@Override
	public Stream<TemporalAmount> repeatingStream() {
		return Stream.iterate(
						0,
						day ->
								repetitionStartDate
										.plusDays(day)
										.isBefore(repetitionEndDate.plusDays(1L)),
						day -> day + repetitionPeriod)
				.map(Period::ofDays);
	}
}
