package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import com.sillim.recordit.task.domain.vo.TaskWeekdayBit;
import jakarta.persistence.Entity;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskWeeklyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskWeeklyRepetitionPattern(
			final TaskRepetitionType repetitionType,
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final TaskWeekdayBit weekdayBit,
			final TaskGroup taskGroup) {
		super(
				repetitionType,
				repetitionPeriod,
				repetitionStartDate,
				repetitionEndDate,
				null,
				null,
				null,
				null,
				weekdayBit,
				taskGroup);
	}

	public static TaskRepetitionPattern createWeekly(
			final Integer repetitionPeriod,
			final LocalDate repetitionStartDate,
			final LocalDate repetitionEndDate,
			final Integer weekdayBit,
			final TaskGroup taskGroup) {
		return TaskWeeklyRepetitionPattern.builder()
				.repetitionType(TaskRepetitionType.WEEKLY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.weekdayBit(new TaskWeekdayBit(weekdayBit))
				.taskGroup(taskGroup)
				.build();
	}

	@Override
	public Stream<TemporalAmount> repeatingStream() {
		return Stream.iterate(
						getRepetitionStartDate(),
						date -> date.isBefore(getRepetitionEndDate().plusDays(1L)),
						date ->
								date.plusWeeks(getRepetitionPeriod())
										.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)))
				.flatMap(
						startDate ->
								Stream.iterate(
												startDate,
												date ->
														date.isBefore(
																		getRepetitionEndDate()
																				.plusDays(1L))
																&& date.isBefore(
																		startDate.with(
																				TemporalAdjusters
																						.next(
																								DayOfWeek
																										.SUNDAY))),
												date -> date.plusDays(1L))
										.filter(
												date ->
														getWeekdayBit()
																.isValidWeekday(
																		date.getDayOfWeek())))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														getRepetitionStartDate(), date)));
	}
}
