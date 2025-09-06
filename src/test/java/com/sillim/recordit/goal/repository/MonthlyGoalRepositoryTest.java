package com.sillim.recordit.goal.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.fixture.MonthlyGoalFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
public class MonthlyGoalRepositoryTest {

	@Autowired MonthlyGoalRepository monthlyGoalRepository;
	@Autowired TestEntityManager em;

	long memberId = 1L;
	private ScheduleCategory category;
	private CalendarCategory calendarCategory;
	private Calendar calendar;

	@BeforeEach
	void beforeEach() {
		calendarCategory =
				em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId));
		calendar = em.persist(CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId));
		category = em.persist(ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar));
	}

	@Test
	@DisplayName("새로운 월 목표 레코드를 저장한다.")
	void saveTest() {
		// given
		final MonthlyGoal expected = MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar);
		// when
		MonthlyGoal saved =
				monthlyGoalRepository.save(
						MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar));

		// then
		// 자동 생성 필드가 null이 아닌지 검증
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.getModifiedAt()).isNotNull();

		assertThat(saved)
				.usingRecursiveComparison()
				.ignoringFields("id", "member", "createdAt", "modifiedAt")
				.isEqualTo(expected);
	}

	@Test
	@DisplayName("기존의 월 목표 레코드를 갱신한다.")
	void updateTest() {
		// given
		final MonthlyGoal expected = MonthlyGoalFixture.MODIFIED.getWithMember(category, calendar);
		MonthlyGoal actual =
				em.persist(MonthlyGoalFixture.DEFAULT.getWithMember(category, calendar));

		// when, then
		assertThatCode(
						() -> {
							actual.modify(
									expected.getTitle(),
									expected.getDescription(),
									expected.getStartDate(),
									expected.getEndDate(),
									category,
									calendar);
							em.flush();
						})
				.doesNotThrowAnyException();

		assertThat(actual)
				.usingRecursiveComparison()
				.ignoringFields("id", "member", "createdAt", "modifiedAt")
				.isEqualTo(expected);
	}

	@Test
	@DisplayName("해당 년, 월에 해당하는 월 목표 목록을 조회한다.")
	void findByGoalYearAndGoalMonthAndMember() {
		// given
		final Integer expectedYear = 2024;
		final Integer expectedMonth = 5;
		monthlyGoalRepository.saveAll(
				List.of(
						MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
								LocalDate.of(2024, 5, 1),
								LocalDate.of(2024, 5, 31),
								category,
								calendar),
						MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
								LocalDate.of(2024, 5, 1),
								LocalDate.of(2024, 5, 31),
								category,
								calendar),
						MonthlyGoalFixture.DEFAULT.getWithStartDateAndEndDate(
								LocalDate.of(2024, 6, 1),
								LocalDate.of(2024, 6, 30),
								category,
								calendar)));
		// when
		List<MonthlyGoal> foundList =
				monthlyGoalRepository.findMonthlyGoalInMonth(
						expectedYear, expectedMonth, calendar.getId());
		// then
		assertThat(foundList).hasSize(2);
		for (MonthlyGoal found : foundList) {
			Assertions.assertAll(
					() -> {
						assertThat(found.getStartDate().getYear()).isEqualTo(expectedYear);
						assertThat(found.getStartDate().getMonthValue()).isEqualTo(expectedMonth);
						assertThat(found.getEndDate().getYear()).isEqualTo(expectedYear);
						assertThat(found.getEndDate().getMonthValue()).isEqualTo(expectedMonth);
					});
		}
	}
}
