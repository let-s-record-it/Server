package com.sillim.recordit.task.domain.repetition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import java.time.LocalDate;
import java.time.temporal.TemporalAmount;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskYearlyRepetitionPatternTest {

	TaskGroup taskGroup;

	@BeforeEach
	void init() {
		taskGroup = new TaskGroup(null, null);
	}

	@Test
	@DisplayName("일자 연간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithDateRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithDate(
						1, LocalDate.of(2024, 2, 12), LocalDate.of(2025, 3, 31), 2, 12, taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.YEARLY_WITH_DATE);
	}

	@Test
	@DisplayName("반복 시작 날짜가 반복 월과 일치하지 않으면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfMonthOfYearNotEqualsStartDate() {
		assertThatCode(
						() ->
								TaskYearlyRepetitionPattern.createYearlyWithDate(
										1,
										LocalDate.of(2024, 2, 12),
										LocalDate.of(2025, 3, 31),
										1,
										12,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_TASK_MONTH_OF_YEAR.getDescription());
	}

	@Test
	@DisplayName("반복 시작 날짜가 반복 일자와 일치하지 않으면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfDayOfMonthNotEqualsStartDate() {
		assertThatCode(
						() ->
								TaskYearlyRepetitionPattern.createYearlyWithDate(
										1,
										LocalDate.of(2024, 2, 12),
										LocalDate.of(2025, 3, 31),
										2,
										11,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_TASK_DAY_OF_MONTH.getDescription());
	}

	@Test
	@DisplayName("주차 및 요일 연간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithWeekdayRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithWeekday(
						1,
						LocalDate.of(2024, 2, 12),
						LocalDate.of(2025, 3, 31),
						2,
						WeekNumber.SECOND,
						Weekday.MON,
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.YEARLY_WITH_WEEKDAY);
	}

	@Test
	@DisplayName("반복 주차가 null이라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfWeekNumberIsNull() {
		assertThatCode(
						() ->
								TaskYearlyRepetitionPattern.createYearlyWithWeekday(
										1,
										LocalDate.of(2024, 2, 12),
										LocalDate.of(2025, 3, 31),
										2,
										null,
										Weekday.MON,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NULL_TASK_REPETITION_WEEK_NUMBER.getDescription());
	}

	@Test
	@DisplayName("반복 요일이 null이라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfWeekdayIsNull() {
		assertThatCode(
						() ->
								TaskYearlyRepetitionPattern.createYearlyWithWeekday(
										1,
										LocalDate.of(2024, 2, 12),
										LocalDate.of(2025, 3, 31),
										2,
										WeekNumber.SECOND,
										null,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NULL_TASK_REPETITION_WEEKDAY.getDescription());
	}

	@Test
	@DisplayName("반복 주차가 시작일의 주차와 같지 않다면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfWeekNumberNotEqualsStartDate() {
		assertThatCode(
						() ->
								TaskYearlyRepetitionPattern.createYearlyWithWeekday(
										1,
										LocalDate.of(2024, 2, 12),
										LocalDate.of(2025, 3, 31),
										2,
										WeekNumber.FIRST,
										Weekday.MON,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_TASK_WEEK_NUMBER.getDescription());
	}

	@Test
	@DisplayName("반복 요일이 시작일의 요일과 같지 않다면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfWeekdayNotEqualsStartDate() {
		assertThatCode(
						() ->
								TaskYearlyRepetitionPattern.createYearlyWithWeekday(
										1,
										LocalDate.of(2024, 2, 12),
										LocalDate.of(2025, 3, 31),
										2,
										WeekNumber.SECOND,
										Weekday.TUE,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_TASK_WEEKDAY.getDescription());
	}

	@Test
	@DisplayName("마지막 일자 월간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithLastDayRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithLastDay(
						1, LocalDate.of(2024, 2, 12), LocalDate.of(2025, 3, 31), 2, taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.YEARLY_WITH_LAST_DAY);
	}

	@Test
	@DisplayName("일자 연간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 1인 경우")
	void createYearlyWithDateRepeatingPatternStream_WithPeriod1() {
		// 매년 2월 12일마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithDate(
						1, LocalDate.of(2024, 2, 12), LocalDate.of(2026, 3, 31), 2, 12, taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.YEARLY_WITH_DATE);
					assertThat(repeating).hasSize(3);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 2, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2025, 2, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2026, 2, 12));
				});
	}

	@Test
	@DisplayName("일자 연간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 2인 경우")
	void createYearlyWithDateRepeatingPatternStream_WithPeriod2() {
		// 2년 주기로 2월 12일마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithDate(
						2, LocalDate.of(2024, 2, 12), LocalDate.of(2026, 3, 31), 2, 12, taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.YEARLY_WITH_DATE);
					assertThat(repeating).hasSize(2);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 2, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2026, 2, 12));
				});
	}

	@Test
	@DisplayName("일자 연간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 월에 해당 일자가 존재하지 않는 경우 건너뛴다.")
	void createYearlyWithDateRepeatingPatternStream_SkipIfMonthDoesNotHaveWeekNumber() {
		// 1년 주기로 2월 29일 마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithDate(
						1, LocalDate.of(2024, 2, 29), LocalDate.of(2028, 3, 31), 2, 29, taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.YEARLY_WITH_DATE);
					assertThat(repeating).hasSize(2);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 2, 29));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2028, 2, 29));
				});
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 1일 경우")
	void createYearlyWithWeekdayRepeatingPatternStream_WithPeriod1() {
		// 매월 2번째 금요일 마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithWeekday(
						1,
						LocalDate.of(2024, 2, 12),
						LocalDate.of(2026, 3, 31),
						2,
						WeekNumber.SECOND,
						Weekday.MON,
						taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.YEARLY_WITH_WEEKDAY);
					assertThat(repeating).hasSize(3);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 2, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2025, 2, 10));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2026, 2, 9));
				});
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 2일 경우")
	void createYearlyWithWeekdayRepeatingPatternStream_WithPeriod2() {
		// 2개월 주기로 2번째 금요일 마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithWeekday(
						2,
						LocalDate.of(2024, 2, 12),
						LocalDate.of(2026, 3, 31),
						2,
						WeekNumber.SECOND,
						Weekday.MON,
						taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.YEARLY_WITH_WEEKDAY);
					assertThat(repeating).hasSize(2);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 2, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2026, 2, 9));
				});
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 월에 해당 주차가 존재하지 않으면 건너뛴다.")
	void createYearlyWithWeekdayRepeatingPatternStream_SkipIfMonthDoesNotHaveWeekNumber() {
		// 매월 5번째 수요일 마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithWeekday(
						1,
						LocalDate.of(2023, 12, 31),
						LocalDate.of(2026, 12, 31),
						12,
						WeekNumber.FIFTH,
						Weekday.SUN,
						taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.YEARLY_WITH_WEEKDAY);
					assertThat(repeating).hasSize(2);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2023, 12, 31));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 12, 29));
				});
	}

	@Test
	@DisplayName("마지막 일자 연간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 1인 경우")
	void createYearlyWithLastDayRepeatingPatternStream_WithPeriod1() {
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithLastDay(
						1, LocalDate.of(2024, 2, 29), LocalDate.of(2026, 3, 31), 2, taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.YEARLY_WITH_LAST_DAY);
					assertThat(repeating).hasSize(3);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 2, 29));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2025, 2, 28));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2026, 2, 28));
				});
	}

	@Test
	@DisplayName("마지막 일자 연간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 2인 경우")
	void createYearlyWithLastDayRepeatingPatternStream_WithPeriod2() {
		TaskRepetitionPattern repetitionPattern =
				TaskYearlyRepetitionPattern.createYearlyWithLastDay(
						2, LocalDate.of(2024, 2, 29), LocalDate.of(2026, 3, 31), 2, taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.YEARLY_WITH_LAST_DAY);
					assertThat(repeating).hasSize(2);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 2, 29));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2026, 2, 28));
				});
	}
}
