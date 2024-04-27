package com.sillim.recordit.schedule.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidRepetitionException;
import com.sillim.recordit.member.domain.Auth;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.domain.MemberRole;
import com.sillim.recordit.member.domain.OAuthProvider;
import com.sillim.recordit.schedule.fixture.RepetitionPatternFixture;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RepetitionPatternTest {

	Member member;
	Calendar calendar;
	ScheduleGroup scheduleGroup;

	@BeforeEach
	void initObjects() {
		member =
				Member.builder()
						.auth(new Auth("1234567", OAuthProvider.KAKAO))
						.name("name")
						.job("job")
						.deleted(false)
						.memberRole(List.of(MemberRole.ROLE_USER))
						.build();
		calendar = Calendar.builder().title("title").colorHex("#aabbff").member(member).build();
		scheduleGroup =
				ScheduleGroup.builder().isRepeated(false).member(member).calendar(calendar).build();
	}

	@Test
	@DisplayName("일간 반복 패턴을 생성할 수 있다.")
	void createDailyRepeatingPattern() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.DAILY.getRepetitionPattern(scheduleGroup);

		assertThat(repetitionPattern.getRepetitionType()).isEqualTo(RepetitionType.DAILY);
	}

	@Test
	@DisplayName("주간 반복 패턴을 생성할 수 있다.")
	void createWeeklyRepeatingPattern() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.WEEKLY.getRepetitionPattern(scheduleGroup);

		assertThat(repetitionPattern.getRepetitionType()).isEqualTo(RepetitionType.WEEKLY);
	}

	@Test
	@DisplayName("일자 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithDateRepeatingPattern() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.MONTHLY_WITH_DATE.getRepetitionPattern(scheduleGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(RepetitionType.MONTHLY_WITH_DATE);
	}

	@Test
	@DisplayName("주차 및 요일 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithWeekdayRepeatingPattern() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.MONTHLY_WITH_WEEKDAY.getRepetitionPattern(scheduleGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(RepetitionType.MONTHLY_WITH_WEEKDAY);
	}

	@Test
	@DisplayName("마지막 일자 월간 반복 패턴을 생성할 수 있다.")
	void createMonthlyWithLastDayRepeatingPattern() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.MONTHLY_WITH_LAST_DAY.getRepetitionPattern(scheduleGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(RepetitionType.MONTHLY_WITH_LAST_DAY);
	}

	@Test
	@DisplayName("일자 연간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithDateRepeatingPattern() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.YEARLY_WITH_DATE.getRepetitionPattern(scheduleGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(RepetitionType.YEARLY_WITH_DATE);
	}

	@Test
	@DisplayName("주차 및 요일 연간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithWeekdayRepeatingPattern() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.YEARLY_WITH_WEEKDAY.getRepetitionPattern(scheduleGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(RepetitionType.YEARLY_WITH_WEEKDAY);
	}

	@Test
	@DisplayName("마지막 일자 연간 반복 패턴을 생성할 수 있다.")
	void createYearlyWithLastDayRepeatingPattern() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.YEARLY_WITH_LAST_DAY.getRepetitionPattern(scheduleGroup);

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(RepetitionType.YEARLY_WITH_LAST_DAY);
	}

	@Test
	@DisplayName("일간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다.")
	void createDailyRepeatingPatternScheduleStream() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.DAILY.getRepetitionPattern(scheduleGroup);

		List<TemporalAmount> list = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.DAILY);
					assertThat(list).hasSize(91);
					assertThat(
									RepetitionPatternFixture.DAILY
											.getRepetitionStartDate()
											.plus(list.get(0)))
							.isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
					assertThat(
									RepetitionPatternFixture.WEEKLY
											.getRepetitionStartDate()
											.plus(list.get(90)))
							.isEqualTo(LocalDateTime.of(2024, 3, 31, 0, 0));
				});
	}

	@Test
	@DisplayName("주간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다.")
	void createWeeklyRepeatingPatternScheduleStream() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.WEEKLY.getRepetitionPattern(scheduleGroup);

		List<TemporalAmount> list = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.WEEKLY);
					assertThat(list).hasSize(52);
					assertThat(
									RepetitionPatternFixture.WEEKLY
											.getRepetitionStartDate()
											.plus(list.get(0)))
							.isEqualTo(LocalDateTime.of(2024, 1, 3, 0, 0));
					assertThat(
									RepetitionPatternFixture.WEEKLY
											.getRepetitionStartDate()
											.plus(list.get(51)))
							.isEqualTo(LocalDateTime.of(2024, 3, 31, 0, 0));
				});
	}

	@Test
	@DisplayName("일자 월간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다.")
	void createMonthlyWithDateRepeatingPatternScheduleStream() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.MONTHLY_WITH_DATE.getRepetitionPattern(scheduleGroup);

		List<TemporalAmount> list = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.MONTHLY_WITH_DATE);
					assertThat(list).hasSize(3);
					assertThat(
									RepetitionPatternFixture.MONTHLY_WITH_DATE
											.getRepetitionStartDate()
											.plus(list.get(0)))
							.isEqualTo(LocalDateTime.of(2024, 1, 12, 0, 0));
					assertThat(
									RepetitionPatternFixture.MONTHLY_WITH_DATE
											.getRepetitionStartDate()
											.plus(list.get(2)))
							.isEqualTo(LocalDateTime.of(2024, 3, 12, 0, 0));
				});
	}

	@Test
	@DisplayName("주차 및 요일 월간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다.")
	void createMonthlyWithWeekdayRepeatingPatternScheduleStream() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.MONTHLY_WITH_WEEKDAY.getRepetitionPattern(scheduleGroup);

		List<TemporalAmount> list = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.MONTHLY_WITH_WEEKDAY);
					assertThat(list).hasSize(3);
					assertThat(
									RepetitionPatternFixture.MONTHLY_WITH_WEEKDAY
											.getRepetitionStartDate()
											.plus(list.get(0)))
							.isEqualTo(LocalDateTime.of(2024, 1, 12, 0, 0));
					assertThat(
									RepetitionPatternFixture.MONTHLY_WITH_WEEKDAY
											.getRepetitionStartDate()
											.plus(list.get(1)))
							.isEqualTo(LocalDateTime.of(2024, 2, 9, 0, 0));
					assertThat(
									RepetitionPatternFixture.MONTHLY_WITH_WEEKDAY
											.getRepetitionStartDate()
											.plus(list.get(2)))
							.isEqualTo(LocalDateTime.of(2024, 3, 8, 0, 0));
				});
	}

	@Test
	@DisplayName("마지막 일자 월간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다.")
	void createMonthlyWithLastDayRepeatingPatternScheduleStream() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.MONTHLY_WITH_LAST_DAY.getRepetitionPattern(scheduleGroup);

		List<TemporalAmount> list = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.MONTHLY_WITH_LAST_DAY);
					assertThat(list).hasSize(3);
					assertThat(
									RepetitionPatternFixture.MONTHLY_WITH_LAST_DAY
											.getRepetitionStartDate()
											.plus(list.get(0)))
							.isEqualTo(LocalDateTime.of(2024, 1, 31, 0, 0));
					assertThat(
									RepetitionPatternFixture.MONTHLY_WITH_LAST_DAY
											.getRepetitionStartDate()
											.plus(list.get(1)))
							.isEqualTo(LocalDateTime.of(2024, 2, 29, 0, 0));
					assertThat(
									RepetitionPatternFixture.MONTHLY_WITH_LAST_DAY
											.getRepetitionStartDate()
											.plus(list.get(2)))
							.isEqualTo(LocalDateTime.of(2024, 3, 31, 0, 0));
				});
	}

	@Test
	@DisplayName("일자 연간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다.")
	void createYearlyWithDateRepeatingPatternScheduleStream() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.YEARLY_WITH_DATE.getRepetitionPattern(scheduleGroup);

		List<TemporalAmount> list = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.YEARLY_WITH_DATE);
					assertThat(list).hasSize(2);
					assertThat(
									RepetitionPatternFixture.YEARLY_WITH_DATE
											.getRepetitionStartDate()
											.plus(list.get(0)))
							.isEqualTo(LocalDateTime.of(2024, 2, 12, 0, 0));
					assertThat(
									RepetitionPatternFixture.YEARLY_WITH_DATE
											.getRepetitionStartDate()
											.plus(list.get(1)))
							.isEqualTo(LocalDateTime.of(2025, 2, 12, 0, 0));
				});
	}

	@Test
	@DisplayName("주차 및 요일 연간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다.")
	void createYearlyWithWeekdayRepeatingPatternScheduleStream() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.YEARLY_WITH_WEEKDAY.getRepetitionPattern(scheduleGroup);

		List<TemporalAmount> list = repetitionPattern.repeatingStream().toList();

		assertAll(
				() -> {
					assertThat(repetitionPattern.getRepetitionType())
							.isEqualTo(RepetitionType.YEARLY_WITH_WEEKDAY);
					assertThat(list).hasSize(2);
					assertThat(
									RepetitionPatternFixture.YEARLY_WITH_WEEKDAY
											.getRepetitionStartDate()
											.plus(list.get(0)))
							.isEqualTo(LocalDateTime.of(2024, 2, 12, 0, 0));
					assertThat(
									RepetitionPatternFixture.YEARLY_WITH_WEEKDAY
											.getRepetitionStartDate()
											.plus(list.get(1)))
							.isEqualTo(LocalDateTime.of(2025, 2, 10, 0, 0));
				});
	}

	@Test
	@DisplayName("마지막 일자 연간 반복 패턴에서 시작일부터의 일 수 스트림을 생성할 수 있다.")
	void createYearlyWithLastDayRepeatingPatternScheduleStream() {
		RepetitionPattern repetitionPattern =
				RepetitionPatternFixture.YEARLY_WITH_LAST_DAY.getRepetitionPattern(scheduleGroup);

		List<TemporalAmount> list = repetitionPattern.repeatingStream().toList();

		assertThat(repetitionPattern.getRepetitionType())
				.isEqualTo(RepetitionType.YEARLY_WITH_LAST_DAY);
		assertThat(list).hasSize(2);
		assertThat(
						RepetitionPatternFixture.YEARLY_WITH_LAST_DAY
								.getRepetitionStartDate()
								.plus(list.get(0)))
				.isEqualTo(LocalDateTime.of(2024, 2, 29, 0, 0));
		assertThat(
						RepetitionPatternFixture.YEARLY_WITH_LAST_DAY
								.getRepetitionStartDate()
								.plus(list.get(1)))
				.isEqualTo(LocalDateTime.of(2025, 2, 28, 0, 0));
	}

	@Test
	@DisplayName("반복 주기가 1 미만 999 초과이면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfRepetitionPeriodIs1OrUnderOr999OrOver() {
		assertAll(
				() -> {
					assertThatCode(
									() ->
											RepetitionPatternFixture.PERIOD_1_UNDER
													.getRepetitionPattern(scheduleGroup))
							.isInstanceOf(InvalidRepetitionException.class)
							.hasMessage(ErrorCode.INVALID_REPETITION_PERIOD.getDescription());
					assertThatCode(
									() ->
											RepetitionPatternFixture.PERIOD_999_OVER
													.getRepetitionPattern(scheduleGroup))
							.isInstanceOf(InvalidRepetitionException.class)
							.hasMessage(ErrorCode.INVALID_REPETITION_PERIOD.getDescription());
				});
	}

	@Test
	@DisplayName("반복 시작 시간이 끝 시간보다 크면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfRepetitionStartDateIsAfterEndDate() {
		assertThatCode(
						() ->
								RepetitionPatternFixture.START_IS_AFTER_END.getRepetitionPattern(
										scheduleGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.INVALID_DURATION.getDescription());
	}

	@Test
	@DisplayName("monthOfYear와 시작 시간의 월이 일치하지 않으면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfRepetitionStartDateMonthNotEqualsMonthOfYear() {
		assertThatCode(
						() ->
								RepetitionPatternFixture.NOT_EQUAL_MONTH_OF_YEAR
										.getRepetitionPattern(scheduleGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_MONTH_OF_YEAR.getDescription());
	}

	@Test
	@DisplayName("dayOfMonth와 시작 시간의 일이 일치하지 않으면 InvalidRepetitionException이 발생한다.")
	void throwInvalidRepetitionExceptionIfRepetitionStartDateNotEqualsDayOfMonth() {
		assertThatCode(
						() ->
								RepetitionPatternFixture.NOT_EQUAL_DAY_OF_MONTH
										.getRepetitionPattern(scheduleGroup))
				.isInstanceOf(InvalidRepetitionException.class)
				.hasMessage(ErrorCode.NOT_EQUAL_DAY_OF_MONTH.getDescription());
	}
}
