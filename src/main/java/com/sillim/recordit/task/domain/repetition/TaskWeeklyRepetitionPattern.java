package com.sillim.recordit.task.domain.repetition;

import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.vo.WeekdayBit;
import com.sillim.recordit.task.domain.TaskGroup;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;

class TaskWeeklyRepetitionPattern extends TaskRepetitionPattern {

	@Builder(access = AccessLevel.PRIVATE)
	private TaskWeeklyRepetitionPattern(
			RepetitionType repetitionType,
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			WeekdayBit weekdayBit,
			TaskGroup taskGroup) {
		this.repetitionType = repetitionType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.weekdayBit = weekdayBit;
		this.taskGroup = taskGroup;
	}

	protected static TaskWeeklyRepetitionPattern createWeekly(
			Integer repetitionPeriod,
			LocalDate repetitionStartDate,
			LocalDate repetitionEndDate,
			Integer weekdayBit,
			TaskGroup taskGroup) {
		return TaskWeeklyRepetitionPattern.builder()
				.repetitionType(RepetitionType.WEEKLY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.weekdayBit(new WeekdayBit(weekdayBit))
				.taskGroup(taskGroup)
				.build();
	}

	@Override
	public Stream<TemporalAmount> repeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date ->
								date.plusWeeks(repetitionPeriod)
										.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)))
				.flatMap(
						startDate ->
								Stream.iterate(
												startDate,
												date ->
														date.isBefore(
																		repetitionEndDate.plusDays(
																				1L))
																&& date.isBefore(
																		startDate.with(
																				TemporalAdjusters
																						.next(
																								DayOfWeek
																										.SUNDAY))),
												date -> date.plusDays(1L))
										.filter(
												date ->
														weekdayBit.isValidWeekday(
																date.getDayOfWeek())))
				.map(
						date ->
								Period.ofDays(
										(int) ChronoUnit.DAYS.between(repetitionStartDate, date)));
	}
}
