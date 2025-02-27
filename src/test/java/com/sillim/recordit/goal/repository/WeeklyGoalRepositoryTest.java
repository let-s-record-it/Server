package com.sillim.recordit.goal.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.goal.fixture.WeeklyGoalFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
public class WeeklyGoalRepositoryTest {

	@Autowired WeeklyGoalRepository weeklyGoalRepository;
	@Autowired TestEntityManager em;

	private Member member;
	private ScheduleCategory category;
	private CalendarCategory calendarCategory;
	private Calendar calendar;

	@BeforeEach
	void beforeEach() {
		member = em.persist(MemberFixture.DEFAULT.getMember());
		category = em.persist(ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member));
		calendarCategory = em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(member));
		calendar = em.persist(CalendarFixture.DEFAULT.getCalendar(member, calendarCategory));
	}

	@Test
	@DisplayName("새로운 주 목표 레코드를 저장한다.")
	void save() {
		// given
		final WeeklyGoal expected = WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar);
		// when
		WeeklyGoal saved =
				weeklyGoalRepository.save(
						WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar));

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
	@DisplayName("해당 년, 월에 해당하는 주 목표 목록을 조회한다.")
	void findByGoalYearAndGoalMonthAndMember() {
		// given
		final Integer expectedYear = 2024;
		final Integer expectedMonth = 8;
		weeklyGoalRepository.saveAll(
				List.of(
						WeeklyGoalFixture.DEFAULT.getWithWeekAndStartDateAndEndDate(
								2,
								LocalDate.of(2024, 8, 4),
								LocalDate.of(2024, 8, 10),
								category,
								member,
								calendar),
						WeeklyGoalFixture.DEFAULT.getWithWeekAndStartDateAndEndDate(
								5,
								LocalDate.of(2024, 8, 25),
								LocalDate.of(2024, 8, 31),
								category,
								member,
								calendar),
						WeeklyGoalFixture.DEFAULT.getWithWeekAndStartDateAndEndDate(
								1,
								LocalDate.of(2024, 9, 1),
								LocalDate.of(2024, 9, 7),
								category,
								member,
								calendar)));
		// when
		List<WeeklyGoal> foundList =
				weeklyGoalRepository.findWeeklyGoalInMonth(
						expectedYear, expectedMonth, calendar.getId());
		// then
		assertThat(foundList).hasSize(2);
		for (WeeklyGoal found : foundList) {
			Assertions.assertAll(
					() -> {
						assertThat(found.getStartDate().getYear()).isEqualTo(expectedYear);
						assertThat(found.getStartDate().getMonthValue()).isEqualTo(expectedMonth);
						assertThat(found.getEndDate().getYear()).isEqualTo(expectedYear);
						assertThat(found.getEndDate().getMonthValue()).isEqualTo(expectedMonth);
					});
		}
	}

	@Test
	@DisplayName("해당 년, 월에 해당하는 주 목표 목록을 조회한다. - 두 개월에 걸쳐 존재하는 주 목표도 함께 조회된다.")
	void findByGoalYearAndGoalMonthAndMemberWhenStuckInTwoMonths() {
		// given
		final Integer expectedYear = 2024;
		final Integer expectedMonth = 7;
		weeklyGoalRepository.saveAll(
				List.of(
						WeeklyGoalFixture.DEFAULT.getWithWeekAndStartDateAndEndDate(
								5,
								LocalDate.of(2024, 7, 28),
								LocalDate.of(2024, 8, 3),
								category,
								member,
								calendar),
						WeeklyGoalFixture.DEFAULT.getWithWeekAndStartDateAndEndDate(
								1,
								LocalDate.of(2024, 9, 1),
								LocalDate.of(2024, 9, 7),
								category,
								member,
								calendar)));
		// when
		List<WeeklyGoal> foundList =
				weeklyGoalRepository.findWeeklyGoalInMonth(
						expectedYear, expectedMonth, calendar.getId());
		// then
		assertThat(foundList).hasSize(1);
		for (WeeklyGoal found : foundList) {
			Assertions.assertAll(
					() -> {
						assertThat(found.getStartDate().getYear()).isEqualTo(expectedYear);
						assertThat(found.getStartDate().getMonthValue()).isEqualTo(expectedMonth);
					});
		}
	}

	@Test
	@DisplayName("id에 해당하는 주 목표 레코드를 조회한다.")
	void findWeeklyGoalByIdTest() {
		WeeklyGoal expected = WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar);
		WeeklyGoal saved =
				weeklyGoalRepository.save(
						WeeklyGoalFixture.DEFAULT.getWithMember(category, calendar));

		Optional<WeeklyGoal> found = weeklyGoalRepository.findWeeklyGoalById(saved.getId());

		assertThat(found).isNotEmpty();
		assertThat(found.get())
				.usingRecursiveComparison()
				.ignoringFields("id", "member", "createdAt", "modifiedAt")
				.isEqualTo(expected);
	}
}
