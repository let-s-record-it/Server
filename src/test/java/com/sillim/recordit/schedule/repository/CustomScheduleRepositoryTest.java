package com.sillim.recordit.schedule.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.fixture.ScheduleFixture;
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
	ScheduleGroup scheduleGroup;

	@BeforeEach
	void setEntities() {
		member = em.persist(MemberFixture.DEFAULT.getMember());
		calendar = em.persist(CalendarFixture.DEFAULT.getCalendar(member));
		scheduleGroup = em.persist(new ScheduleGroup(false));
	}

	@Test
	@DisplayName("년 월에 맞는 일정을 조회한다.")
	void searchSchedules() {
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2023, 2, 1, 0, 0),
						LocalDateTime.of(2024, 1, 1, 0, 0)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2023, 2, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2025, 1, 1, 0, 0)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2024, 2, 1, 0, 0),
						LocalDateTime.of(2025, 1, 1, 0, 0)));
		scheduleRepository.save(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2024, 3, 1, 0, 0),
						LocalDateTime.of(2025, 1, 1, 0, 0)));

		List<Schedule> scheduleInMonth =
				customScheduleRepository.findScheduleInMonth(calendar.getId(), 2024, 2);

		scheduleInMonth.forEach(
				schedule -> {
					System.out.println(
							schedule.getScheduleDuration().getStartDatetime()
									+ " => "
									+ schedule.getScheduleDuration().getEndDatetime());
				});

		assertThat(scheduleInMonth).hasSize(3);
	}
}
