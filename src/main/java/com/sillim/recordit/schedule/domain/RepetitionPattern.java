package com.sillim.recordit.schedule.domain;

import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.schedule.domain.vo.DayOfMonth;
import com.sillim.recordit.schedule.domain.vo.MonthOfYear;
import com.sillim.recordit.schedule.domain.vo.WeekdayBit;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepetitionPattern extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "repetition_pattern_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RepetitionType repetitionType;

	@Column(nullable = false)
	private Integer repetitionPeriod;

	@Column(nullable = false)
	private LocalDateTime repetitionStartDate;

	@Column(nullable = false)
	private LocalDateTime repetitionEndDate;

	@Embedded private MonthOfYear monthOfYear;

	@Embedded private DayOfMonth dayOfMonth;

	@Column private WeekNumber weekNumber;

	@Column private Weekday weekday;

	@Embedded private WeekdayBit weekdayBit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_group_id", unique = true)
	private ScheduleGroup scheduleGroup;

	@Builder(access = AccessLevel.PRIVATE)
	private RepetitionPattern(
			RepetitionType repetitionType,
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			MonthOfYear monthOfYear,
			DayOfMonth dayOfMonth,
			WeekNumber weekNumber,
			Weekday weekday,
			WeekdayBit weekdayBit,
			ScheduleGroup scheduleGroup) {
		validatePeriod(repetitionPeriod);
		validateDuration(repetitionStartDate, repetitionEndDate);
		this.repetitionType = repetitionType;
		this.repetitionPeriod = repetitionPeriod;
		this.repetitionStartDate = repetitionStartDate;
		this.repetitionEndDate = repetitionEndDate;
		this.monthOfYear = monthOfYear;
		this.dayOfMonth = dayOfMonth;
		this.weekNumber = weekNumber;
		this.weekday = weekday;
		this.weekdayBit = weekdayBit;
		this.scheduleGroup = scheduleGroup;
	}

	private static void validatePeriod(Integer repetitionPeriod) {
		if (repetitionPeriod < 1 || repetitionPeriod > 999) {
			throw new InvalidRepetitionException(ErrorCode.INVALID_REPETITION_PERIOD);
		}
	}

	private static void validateDuration(
			LocalDateTime repetitionStartDate, LocalDateTime repetitionEndDate) {
		if (repetitionStartDate.isAfter(repetitionEndDate)) {
			throw new InvalidRepetitionException(ErrorCode.INVALID_DURATION);
		}
	}

	public static RepetitionPattern createDaily(
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			ScheduleGroup scheduleGroup) {
		return RepetitionPattern.builder()
				.repetitionType(RepetitionType.DAILY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public static RepetitionPattern createWeekly(
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			Integer weekdayBit,
			ScheduleGroup scheduleGroup) {
		return RepetitionPattern.builder()
				.repetitionType(RepetitionType.WEEKLY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.weekdayBit(new WeekdayBit(weekdayBit))
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public static RepetitionPattern createMonthlyWithDate(
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			Integer dayOfMonth,
			ScheduleGroup scheduleGroup) {
		validateDayOfMonthEqualsStartDate(repetitionStartDate, dayOfMonth);
		return RepetitionPattern.builder()
				.repetitionType(RepetitionType.MONTHLY_WITH_DATE)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.dayOfMonth(DayOfMonth.createMonthly(dayOfMonth))
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public static RepetitionPattern createMonthlyWithWeekday(
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			WeekNumber weekNumber,
			Weekday weekday,
			ScheduleGroup scheduleGroup) {
		return RepetitionPattern.builder()
				.repetitionType(RepetitionType.MONTHLY_WITH_WEEKDAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.weekNumber(weekNumber)
				.weekday(weekday)
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public static RepetitionPattern createMonthlyWithLastDay(
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			ScheduleGroup scheduleGroup) {
		return RepetitionPattern.builder()
				.repetitionType(RepetitionType.MONTHLY_WITH_LAST_DAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public static RepetitionPattern createYearlyWithDate(
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			Integer monthOfYear,
			Integer dayOfMonth,
			ScheduleGroup scheduleGroup) {
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		validateDayOfMonthEqualsStartDate(repetitionStartDate, dayOfMonth);
		return RepetitionPattern.builder()
				.repetitionType(RepetitionType.YEARLY_WITH_DATE)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.monthOfYear(new MonthOfYear(monthOfYear))
				.dayOfMonth(DayOfMonth.createYearly(monthOfYear, dayOfMonth))
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public static RepetitionPattern createYearlyWithWeekday(
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			Integer monthOfYear,
			WeekNumber weekNumber,
			Weekday weekday,
			ScheduleGroup scheduleGroup) {
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		return RepetitionPattern.builder()
				.repetitionType(RepetitionType.YEARLY_WITH_WEEKDAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.monthOfYear(new MonthOfYear(monthOfYear))
				.weekNumber(weekNumber)
				.weekday(weekday)
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public static RepetitionPattern createYearlyWithLastDay(
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			Integer monthOfYear,
			ScheduleGroup scheduleGroup) {
		validateMonthOfYearEqualsStartDateMonth(repetitionStartDate, monthOfYear);
		return RepetitionPattern.builder()
				.repetitionType(RepetitionType.YEARLY_WITH_LAST_DAY)
				.repetitionPeriod(repetitionPeriod)
				.repetitionStartDate(repetitionStartDate)
				.repetitionEndDate(repetitionEndDate)
				.monthOfYear(new MonthOfYear(monthOfYear))
				.scheduleGroup(scheduleGroup)
				.build();
	}

	public static RepetitionPattern create(
			RepetitionType repetitionType,
			Integer repetitionPeriod,
			LocalDateTime repetitionStartDate,
			LocalDateTime repetitionEndDate,
			Integer monthOfYear,
			Integer dayOfMonth,
			WeekNumber weekNumber,
			Weekday weekday,
			Integer weekdayBit,
			ScheduleGroup scheduleGroup) {
		if (repetitionType.equals(RepetitionType.DAILY)) {
			return createDaily(
					repetitionPeriod, repetitionStartDate, repetitionEndDate, scheduleGroup);
		}
		if (repetitionType.equals(RepetitionType.WEEKLY)) {
			return createWeekly(
					repetitionPeriod,
					repetitionStartDate,
					repetitionEndDate,
					weekdayBit,
					scheduleGroup);
		}
		if (repetitionType.equals(RepetitionType.MONTHLY_WITH_DATE)) {
			return createMonthlyWithDate(
					repetitionPeriod,
					repetitionStartDate,
					repetitionEndDate,
					dayOfMonth,
					scheduleGroup);
		}
		if (repetitionType.equals(RepetitionType.MONTHLY_WITH_WEEKDAY)) {
			return createMonthlyWithWeekday(
					repetitionPeriod,
					repetitionStartDate,
					repetitionEndDate,
					weekNumber,
					weekday,
					scheduleGroup);
		}
		if (repetitionType.equals(RepetitionType.MONTHLY_WITH_LAST_DAY)) {
			return createMonthlyWithLastDay(
					repetitionPeriod, repetitionStartDate, repetitionEndDate, scheduleGroup);
		}
		if (repetitionType.equals(RepetitionType.YEARLY_WITH_DATE)) {
			return createYearlyWithDate(
					repetitionPeriod,
					repetitionStartDate,
					repetitionEndDate,
					monthOfYear,
					dayOfMonth,
					scheduleGroup);
		}
		if (repetitionType.equals(RepetitionType.YEARLY_WITH_WEEKDAY)) {
			return createYearlyWithWeekday(
					repetitionPeriod,
					repetitionStartDate,
					repetitionEndDate,
					monthOfYear,
					weekNumber,
					weekday,
					scheduleGroup);
		}
		if (repetitionType.equals(RepetitionType.YEARLY_WITH_LAST_DAY)) {
			return createYearlyWithLastDay(
					repetitionPeriod,
					repetitionStartDate,
					repetitionEndDate,
					monthOfYear,
					scheduleGroup);
		}
		throw new InvalidRepetitionException(ErrorCode.INVALID_REPETITION_TYPE);
	}

	public Stream<TemporalAmount> repeatingStream() {
		if (repetitionType.equals(RepetitionType.DAILY)) {
			return dailyRepeatingStream();
		}
		if (repetitionType.equals(RepetitionType.WEEKLY)) {
			return weeklyRepeatingStream();
		}
		if (repetitionType.equals(RepetitionType.MONTHLY_WITH_DATE)) {
			return monthlyWithDateRepeatingStream();
		}
		if (repetitionType.equals(RepetitionType.MONTHLY_WITH_WEEKDAY)) {
			return monthlyWithWeekdayRepeatingStream();
		}
		if (repetitionType.equals(RepetitionType.MONTHLY_WITH_LAST_DAY)) {
			return monthlyWithLastDayRepeatingStream();
		}
		if (repetitionType.equals(RepetitionType.YEARLY_WITH_DATE)) {
			return yearlyWithDateRepeatingStream();
		}
		if (repetitionType.equals(RepetitionType.YEARLY_WITH_WEEKDAY)) {
			return yearlyWithWeekdayRepeatingStream();
		}
		if (repetitionType.equals(RepetitionType.YEARLY_WITH_LAST_DAY)) {
			return yearlyWithLastDayRepeatingStream();
		}
		throw new InvalidRepetitionException(ErrorCode.INVALID_REPETITION_TYPE);
	}

	private Stream<TemporalAmount> dailyRepeatingStream() {
		return Stream.iterate(
						0,
						day ->
								repetitionStartDate
										.plusDays(day)
										.isBefore(repetitionEndDate.plusDays(1L)),
						day -> day + repetitionPeriod)
				.map(Period::ofDays);
	}

	private Stream<TemporalAmount> weeklyRepeatingStream() {
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

	private Stream<TemporalAmount> monthlyWithDateRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date -> date.plusMonths(repetitionPeriod))
				.map(
						date ->
								Period.ofDays(
										(int) ChronoUnit.DAYS.between(repetitionStartDate, date)));
	}

	private Stream<TemporalAmount> monthlyWithWeekdayRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date ->
								date.isBefore(
										repetitionEndDate
												.with(TemporalAdjusters.lastDayOfMonth())
												.plusDays(1L)),
						date -> date.plusMonths(repetitionPeriod))
				.filter(date -> findDateByWeekday(date).isBefore(repetitionEndDate))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														repetitionStartDate,
														findDateByWeekday(date))));
	}

	private Stream<TemporalAmount> monthlyWithLastDayRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date -> date.plusMonths(repetitionPeriod))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														repetitionStartDate,
														date.with(
																TemporalAdjusters
																		.lastDayOfMonth()))));
	}

	private Stream<TemporalAmount> yearlyWithDateRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date -> date.plusYears(repetitionPeriod))
				.filter(date -> dayOfMonth.equalsDayOfMonthValue(date.getDayOfMonth()))
				.map(
						date ->
								Period.ofDays(
										(int) ChronoUnit.DAYS.between(repetitionStartDate, date)));
	}

	private Stream<TemporalAmount> yearlyWithWeekdayRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date ->
								date.isBefore(
										repetitionEndDate
												.with(TemporalAdjusters.lastDayOfMonth())
												.plusDays(1L)),
						date -> date.plusYears(repetitionPeriod))
				.filter(date -> findDateByWeekday(date).isBefore(repetitionEndDate))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														repetitionStartDate,
														findDateByWeekday(date))));
	}

	private Stream<TemporalAmount> yearlyWithLastDayRepeatingStream() {
		return Stream.iterate(
						repetitionStartDate,
						date -> date.isBefore(repetitionEndDate.plusDays(1L)),
						date -> date.plusYears(repetitionPeriod))
				.map(
						date ->
								Period.ofDays(
										(int)
												ChronoUnit.DAYS.between(
														repetitionStartDate,
														date.with(
																TemporalAdjusters
																		.lastDayOfMonth()))));
	}

	private LocalDateTime findDateByWeekday(LocalDateTime date) {
		LocalDateTime firstDayOfMonth = date.withDayOfMonth(1);
		DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();

		int daysUntilWeekday = weekday.getValue() - firstDayOfWeek.getValue();
		if (daysUntilWeekday < 0) {
			daysUntilWeekday += 7;
		}

		return firstDayOfMonth.plusDays(daysUntilWeekday + ((weekNumber.getValue() - 1) * 7L));
	}

	private static void validateDayOfMonthEqualsStartDate(
			LocalDateTime startDate, Integer dayOfMonth) {
		if (startDate.getDayOfMonth() != dayOfMonth) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_DAY_OF_MONTH);
		}
	}

	private static void validateMonthOfYearEqualsStartDateMonth(
			LocalDateTime startDate, Integer monthOfYear) {
		if (startDate.getMonth().getValue() != monthOfYear) {
			throw new InvalidRepetitionException(ErrorCode.NOT_EQUAL_MONTH_OF_YEAR);
		}
	}
}