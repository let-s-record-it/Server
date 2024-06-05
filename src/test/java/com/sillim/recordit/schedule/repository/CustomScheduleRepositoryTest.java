package com.sillim.recordit.schedule.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.fixture.ScheduleFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
class CustomScheduleRepositoryTest {

	@Qualifier("customScheduleRepositoryImpl") @Autowired
	CustomScheduleRepository customScheduleRepository;

	@Autowired ScheduleRepository scheduleRepository;
	@Autowired TestEntityManager em;

	Member member;
	Calendar calendar;

	@BeforeEach
	void setEntities() {
		member = em.persist(MemberFixture.DEFAULT.getMember());
		calendar = em.persist(CalendarFixture.DEFAULT.getCalendar(member));
	}

	@Test
	@DisplayName("년 월에 맞는 일정을 조회한다.")
	void searchSchedules() {
		ScheduleGroup scheduleGroup1 = em.persist(new ScheduleGroup(false));
		ScheduleGroup scheduleGroup2 = em.persist(new ScheduleGroup(false));
		ScheduleGroup scheduleGroup3 = em.persist(new ScheduleGroup(false));
		ScheduleGroup scheduleGroup4 = em.persist(new ScheduleGroup(false));
		ScheduleGroup scheduleGroup5 = em.persist(new ScheduleGroup(false));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup1,
						calendar,
						LocalDateTime.of(2023, 2, 1, 0, 0),
						LocalDateTime.of(2024, 1, 1, 0, 0)));
		Schedule schedule2 =
				scheduleRepository.save(
						ScheduleFixture.DEFAULT.getSchedule(
								scheduleGroup2,
								calendar,
								LocalDateTime.of(2023, 2, 1, 0, 0),
								LocalDateTime.of(2024, 2, 1, 0, 0)));
		Schedule schedule3 =
				scheduleRepository.save(
						ScheduleFixture.DEFAULT.getSchedule(
								scheduleGroup3,
								calendar,
								LocalDateTime.of(2024, 1, 1, 0, 0),
								LocalDateTime.of(2025, 1, 1, 0, 0)));
		Schedule schedule4 =
				scheduleRepository.save(
						ScheduleFixture.DEFAULT.getSchedule(
								scheduleGroup4,
								calendar,
								LocalDateTime.of(2024, 2, 1, 0, 0),
								LocalDateTime.of(2025, 1, 1, 0, 0)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup5,
						calendar,
						LocalDateTime.of(2024, 3, 1, 0, 0),
						LocalDateTime.of(2025, 1, 1, 0, 0)));

		List<Schedule> scheduleInMonth =
				customScheduleRepository.findScheduleInMonth(calendar.getId(), 2024, 2);

		assertThat(scheduleInMonth).hasSize(3);
		assertThat(scheduleInMonth).containsExactlyInAnyOrder(schedule2, schedule3, schedule4);
	}

	@Test
	@DisplayName("년 월 일에 맞는 일정을 조회한다.")
	void searchSchedulesInDay() {
		ScheduleGroup scheduleGroup1 = em.persist(new ScheduleGroup(false));
		ScheduleGroup scheduleGroup2 = em.persist(new ScheduleGroup(false));
		ScheduleGroup scheduleGroup3 = em.persist(new ScheduleGroup(false));
		ScheduleGroup scheduleGroup4 = em.persist(new ScheduleGroup(false));
		ScheduleGroup scheduleGroup5 = em.persist(new ScheduleGroup(false));
		em.persist(
				RepetitionPattern.createDaily(
						1,
						LocalDateTime.of(2023, 2, 1, 0, 0),
						LocalDateTime.of(2023, 3, 1, 0, 0),
						scheduleGroup2));
		em.persist(
				RepetitionPattern.createDaily(
						1,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						scheduleGroup3));
		em.persist(
				RepetitionPattern.createDaily(
						1,
						LocalDateTime.of(2024, 2, 1, 0, 0),
						LocalDateTime.of(2024, 3, 1, 0, 0),
						scheduleGroup4));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup1,
						calendar,
						LocalDateTime.of(2023, 2, 1, 0, 0),
						LocalDateTime.of(2024, 1, 1, 0, 0)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup2,
						calendar,
						LocalDateTime.of(2023, 2, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup3,
						calendar,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 23, 59)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup4,
						calendar,
						LocalDateTime.of(2024, 2, 1, 0, 0),
						LocalDateTime.of(2025, 1, 1, 0, 0)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup5,
						calendar,
						LocalDateTime.of(2024, 3, 1, 0, 0),
						LocalDateTime.of(2025, 1, 1, 0, 0)));

		List<Schedule> scheduleInDay =
				customScheduleRepository.findScheduleInDay(
						calendar.getId(), LocalDate.of(2024, 2, 1));

		assertThat(scheduleInDay).hasSize(3);
		assertThat(scheduleInDay.get(0).getScheduleGroup()).isNotNull();
		assertThat(scheduleInDay.get(0).getScheduleGroup().getRepetitionPattern()).isNotNull();
	}
}
