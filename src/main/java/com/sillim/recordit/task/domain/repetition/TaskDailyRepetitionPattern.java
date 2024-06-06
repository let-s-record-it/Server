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
class TaskDailyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskDailyRepetitionPattern(
			final TaskRepetitionType repetitionType,
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskGroup taskGroup) {
		this.repetitionType = repetitionType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.taskGroup = taskGroup;
	}

	protected static TaskDailyRepetitionPattern createDaily(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskGroup taskGroup) {
		return TaskDailyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.DAILY)
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
