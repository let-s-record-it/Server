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

class TaskMonthlyRepetitionPatternTest {

	TaskGroup taskGroup;

	@BeforeEach
	void init() {
		taskGroup = new TaskGroup(null, null);
	}

	@Test
	@DisplayName("일자 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithDateRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithDate(
						1, LocalDate.of(2024, 1, 31), LocalDate.of(2024, 5, 31), 31, taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.MONTHLY_WITH_DATE);
	}

	@Test
	@DisplayName("반복 시작 일이 반복 일자와 일치하지 않으면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfDayOfMonthNotEqualsStartDate() {
		assertThatCode(
						() ->
								TaskMonthlyRepetitionPattern.createMonthlyWithDate(
										1,
										LocalDate.of(2024, 1, 31),
										LocalDate.of(2024, 5, 31),
										30,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_TASK_DAY_OF_MONTH.getDescription());
	}

	@Test
	@DisplayName("주차 및 요일 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithWeekdayRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
						1,
						LocalDate.of(2024, 1, 31),
						LocalDate.of(2024, 5, 31),
						WeekNumber.FIFTH,
						Weekday.WED,
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.MONTHLY_WITH_WEEKDAY);
	}

	@Test
	@DisplayName("반복 주차가 null이라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfWeekNumberIsNull() {
		assertThatCode(
						() ->
								TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
										1,
										LocalDate.of(2024, 1, 31),
										LocalDate.of(2024, 5, 31),
										null,
										Weekday.WED,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NULL_TASK_REPETITION_WEEK_NUMBER.getDescription());
	}

	@Test
	@DisplayName("반복 요일이 null이라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfWeekdayIsNull() {
		assertThatCode(
						() ->
								TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
										1,
										LocalDate.of(2024, 1, 31),
										LocalDate.of(2024, 5, 31),
										WeekNumber.FIFTH,
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
								TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
										1,
										LocalDate.of(2024, 1, 31),
										LocalDate.of(2024, 5, 31),
										WeekNumber.FOURTH,
										Weekday.WED,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_TASK_WEEK_NUMBER.getDescription());
	}

	@Test
	@DisplayName("반복 요일이 시작일의 요일과 같지 않다면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfWeekdayNotEqualsStartDate() {
		assertThatCode(
						() ->
								TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
										1,
										LocalDate.of(2024, 1, 31),
										LocalDate.of(2024, 5, 31),
										WeekNumber.FIFTH,
										Weekday.THU,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_TASK_WEEKDAY.getDescription());
	}

	@Test
	@DisplayName("마지막 일자 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithLastDayRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithLastDay(
						1, LocalDate.of(2024, 1, 31), LocalDate.of(2024, 5, 31), taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.MONTHLY_WITH_LAST_DAY);
	}

	@Test
	@DisplayName("일자 월간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 1인 경우")
	void createMonthlyWithDateRepeatingPatternStream_WithPeriod1() {
		// 매월 12일마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithDate(
						1, LocalDate.of(2024, 1, 12), LocalDate.of(2024, 5, 12), 12, taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.MONTHLY_WITH_DATE);
					assertThat(repeating).hasSize(5);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 2, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2024, 3, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(3)))
							.isEqualTo(LocalDate.of(2024, 4, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(4)))
							.isEqualTo(LocalDate.of(2024, 5, 12));
				});
	}

	@Test
	@DisplayName("일자 월간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 2인 경우")
	void createMonthlyWithDateRepeatingPatternStream_WithPeriod2() {
		// 2개월 주기로 12일마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithDate(
						2, LocalDate.of(2024, 1, 12), LocalDate.of(2024, 5, 12), 12, taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.MONTHLY_WITH_DATE);
					assertThat(repeating).hasSize(3);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 3, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2024, 5, 12));
				});
	}

	@Test
	@DisplayName("일자 월간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 월에 해당 일자가 존재하지 않는 경우 건너뛴다.")
	void createMonthlyWithDateRepeatingPatternStream_SkipIfMonthDoesNotHaveWeekNumber() {
		// 매월 31일마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithDate(
						1, LocalDate.of(2024, 1, 31), LocalDate.of(2024, 5, 31), 31, taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.MONTHLY_WITH_DATE);
					assertThat(repeating).hasSize(3);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 31));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 3, 31));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2024, 5, 31));
				});
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 1일 경우")
	void createMonthlyWithWeekdayRepeatingPatternStream_WithPeriod1() {
		// 매월 2번째 금요일 마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
						1,
						LocalDate.of(2024, 1, 12),
						LocalDate.of(2024, 5, 31),
						WeekNumber.SECOND,
						Weekday.FRI,
						taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.MONTHLY_WITH_WEEKDAY);
					assertThat(repeating).hasSize(5);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 2, 9));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2024, 3, 8));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(3)))
							.isEqualTo(LocalDate.of(2024, 4, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(4)))
							.isEqualTo(LocalDate.of(2024, 5, 10));
				});
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 2일 경우")
	void createMonthlyWithWeekdayRepeatingPatternStream_WithPeriod2() {
		// 2개월 주기로 2번째 금요일 마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
						2,
						LocalDate.of(2024, 1, 12),
						LocalDate.of(2024, 5, 31),
						WeekNumber.SECOND,
						Weekday.FRI,
						taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.MONTHLY_WITH_WEEKDAY);
					assertThat(repeating).hasSize(3);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 12));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 3, 8));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2024, 5, 10));
				});
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 월에 해당 주차가 존재하지 않으면 건너뛴다.")
	void createMonthlyWithWeekdayRepeatingPatternStream_SkipIfMonthDoesNotHaveWeekNumber() {
		// 매월 5번째 수요일 마다 반복
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithWeekday(
						1,
						LocalDate.of(2024, 1, 31),
						LocalDate.of(2024, 5, 31),
						WeekNumber.FIFTH,
						Weekday.WED,
						taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.MONTHLY_WITH_WEEKDAY);
					assertThat(repeating).hasSize(2);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 31));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 5, 29));
				});
	}

	@Test
	@DisplayName("마지막 일자 월간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 1인 경우")
	void createMonthlyWithLastDayRepeatingPatternStream_WithPeriod1() {
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithLastDay(
						1, LocalDate.of(2024, 1, 31), LocalDate.of(2024, 3, 31), taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.MONTHLY_WITH_LAST_DAY);
					assertThat(repeating).hasSize(3);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 31));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 2, 29));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(2)))
							.isEqualTo(LocalDate.of(2024, 3, 31));
				});
	}

	@Test
	@DisplayName("마지막 일자 월간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다. - 주기가 2인 경우")
	void createMonthlyWithLastDayRepeatingPatternStream_WithPeriod2() {
		TaskRepetitionPattern repetitionPattern =
				TaskMonthlyRepetitionPattern.createMonthlyWithLastDay(
						2, LocalDate.of(2024, 1, 31), LocalDate.of(2024, 3, 31), taskGroup);

		List<TemporalAmount> repeating = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(TaskRepetitionType.MONTHLY_WITH_LAST_DAY);
					assertThat(repeating).hasSize(2);
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(0)))
							.isEqualTo(LocalDate.of(2024, 1, 31));
					assertThat(repetitionPattern.getRepetitionStartDate().plus(repeating.get(1)))
							.isEqualTo(LocalDate.of(2024, 3, 31));
				});
	}
}
