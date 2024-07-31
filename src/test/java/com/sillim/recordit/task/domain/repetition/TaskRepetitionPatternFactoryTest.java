package com.sillim.recordit.task.domain.repetition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskRepetitionPatternFactoryTest {

	TaskGroup taskGroup;

	@BeforeEach
	void init() {
		taskGroup = new TaskGroup(null, null);
	}

	@Test
	@DisplayName("일간 반복 패턴을 생성할 수 있다.")
	void createDailyRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskRepetitionPatternFactory.create(
						TaskRepetitionType.DAILY,
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						null,
						taskGroup);
		assertThat(repetitionPattern.getRepetitionType()).isEqualTo(TaskRepetitionType.DAILY);
	}

	@Test
	@DisplayName("주간 반복 패턴을 생성할 수 있다.")
	void createWeeklyRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskRepetitionPatternFactory.create(
						TaskRepetitionType.WEEKLY,
						1,
						LocalDate.of(2024, 1, 1),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						Integer.parseInt("1110100", 2), // 수 금 토 일
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType()).isEqualTo(TaskRepetitionType.WEEKLY);
	}

	@Test
	@DisplayName("일자 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithDateRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskRepetitionPatternFactory.create(
						TaskRepetitionType.MONTHLY_WITH_DATE,
						1,
						LocalDate.of(2024, 1, 12),
						LocalDate.of(2024, 3, 31),
						null,
						12,
						null,
						null,
						null,
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.MONTHLY_WITH_DATE);
	}

	@Test
	@DisplayName("주차 및 요일 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithWeekdayRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskRepetitionPatternFactory.create(
						TaskRepetitionType.MONTHLY_WITH_WEEKDAY,
						1,
						LocalDate.of(2024, 1, 12),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						WeekNumber.SECOND,
						Weekday.FRI,
						null,
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.MONTHLY_WITH_WEEKDAY);
	}

	@Test
	@DisplayName("마지막 일자 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithLastDayRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskRepetitionPatternFactory.create(
						TaskRepetitionType.MONTHLY_WITH_LAST_DAY,
						1,
						LocalDate.of(2024, 1, 31),
						LocalDate.of(2024, 3, 31),
						null,
						null,
						null,
						null,
						null,
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.MONTHLY_WITH_LAST_DAY);
	}

	@Test
	@DisplayName("일자 연간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithDateRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskRepetitionPatternFactory.create(
						TaskRepetitionType.YEARLY_WITH_DATE,
						1,
						LocalDate.of(2024, 2, 12),
						LocalDate.of(2025, 3, 31),
						2,
						12,
						null,
						null,
						null,
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.YEARLY_WITH_DATE);
	}

	@Test
	@DisplayName("주차 및 요일 연간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithWeekdayRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskRepetitionPatternFactory.create(
						TaskRepetitionType.YEARLY_WITH_WEEKDAY,
						1,
						LocalDate.of(2024, 2, 12),
						LocalDate.of(2025, 3, 31),
						2,
						null,
						WeekNumber.SECOND,
						Weekday.MON,
						null,
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.YEARLY_WITH_WEEKDAY);
	}

	@Test
	@DisplayName("마지막 일자 연간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithLastDayRepeatingPattern() {
		TaskRepetitionPattern repetitionPattern =
				TaskRepetitionPatternFactory.create(
						TaskRepetitionType.YEARLY_WITH_LAST_DAY,
						1,
						LocalDate.of(2024, 2, 29),
						LocalDate.of(2025, 3, 31),
						2,
						null,
						null,
						null,
						null,
						taskGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(TaskRepetitionType.YEARLY_WITH_LAST_DAY);
	}

	@Test
	@DisplayName("반복 타입이 null이라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfRepetitionTypeIsNull() {

		assertThatCode(
						() ->
								TaskRepetitionPatternFactory.create(
										null,
										1,
										LocalDate.of(2024, 1, 1),
										LocalDate.of(2024, 3, 31),
										null,
										null,
										null,
										null,
										null,
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NULL_TASK_REPETITION_TYPE.getDescription());
	}
}
