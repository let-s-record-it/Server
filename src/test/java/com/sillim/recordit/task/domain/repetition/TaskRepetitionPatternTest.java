package com.sillim.recordit.task.domain.repetition;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.mock.MockTaskRepetitionPattern;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TaskRepetitionPatternTest {

	TaskGroup taskGroup;

	@BeforeEach
	void init() {
		taskGroup = new TaskGroup(false, null, null);
	}

	@Test
	@DisplayName("반복 주기가 null이라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfPeriodIsNull() {

		assertThatCode(
						() ->
								new MockTaskRepetitionPattern(
										null,
										LocalDate.of(2024, 1, 1),
										LocalDate.of(2024, 3, 31),
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NULL_TASK_REPETITION_PERIOD.getDescription());
	}

	@Test
	@DisplayName("반복 주기가 1미만이라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfPeriodIsLessThan1() {

		assertThatCode(
						() ->
								new MockTaskRepetitionPattern(
										0,
										LocalDate.of(2024, 1, 1),
										LocalDate.of(2024, 3, 31),
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.TASK_REPETITION_PERIOD_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("반복 주기가 999초과라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfPeriodIsGreaterThan999() {

		assertThatCode(
						() ->
								new MockTaskRepetitionPattern(
										1000,
										LocalDate.of(2024, 1, 1),
										LocalDate.of(2024, 3, 31),
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.TASK_REPETITION_PERIOD_OUT_OF_RANGE.getDescription());
	}

	@Test
	@DisplayName("반복 시작/종료일이 null이라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfStartDateOrEndDateIsNull() {

		assertAll(
				() -> {
					// case 1. 시작일이 null
					assertThatCode(
									() ->
											new MockTaskRepetitionPattern(
													1, null, LocalDate.of(2024, 3, 31), taskGroup))
							.isInstanceOf(InvalidRepetitionException.class)
							.hasMessage(ErrorCode.NULL_TASK_REPETITION_DURATION.getDescription());
					// case 2. 종료일이 null
					assertThatCode(
									() ->
											new MockTaskRepetitionPattern(
													1, LocalDate.of(2024, 1, 1), null, taskGroup))
							.isInstanceOf(InvalidRepetitionException.class)
							.hasMessage(ErrorCode.NULL_TASK_REPETITION_DURATION.getDescription());
					// case 3. 시작일, 종료일이 null
					assertThatCode(() -> new MockTaskRepetitionPattern(1, null, null, taskGroup))
							.isInstanceOf(InvalidRepetitionException.class)
							.hasMessage(ErrorCode.NULL_TASK_REPETITION_DURATION.getDescription());
				});
	}

	@Test
	@DisplayName("반복 시작일이 종료일 이후라면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfStartDateIsAfterEndDate() {

		assertThatCode(
						() ->
								new MockTaskRepetitionPattern(
										1,
										LocalDate.of(2024, 3, 31),
										LocalDate.of(2024, 1, 1),
										taskGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.TASK_REPETITION_INVALID_DURATION.getDescription());
	}
}
