package com.sillim.recordit.schedule.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.schedule.domain.RepetitionPattern;
import com.sillim.recordit.schedule.domain.Schedule;
import com.sillim.recordit.schedule.domain.ScheduleGroup;
import com.sillim.recordit.schedule.dto.request.ScheduleAddRequest;
import com.sillim.recordit.schedule.fixture.ScheduleFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
	CalendarCategory category;
	Calendar calendar;

	@BeforeEach
	void setEntities() {
		member = em.persist(MemberFixture.DEFAULT.getMember());
		category = em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(member));
		calendar = em.persist(CalendarFixture.DEFAULT.getCalendar(member, category));
	}

	@Test
	@DisplayName("schedule 저장 시 scheduleAlarm은 연관관계로 인해 같이 persist 된다.")
	void saveSchedule() {
		ScheduleGroup scheduleGroup = em.persist(new ScheduleGroup(false));
		ScheduleAddRequest scheduleAddRequest =
				new ScheduleAddRequest(
						"title",
						"description",
						false,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						false,
						null,
						"aaffbb",
						"서울역",
						true,
						36.0,
						127.0,
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)));

		Schedule schedule =
				scheduleRepository.save(scheduleAddRequest.toSchedule(calendar, scheduleGroup));

		assertThat(schedule.getScheduleAlarms()).hasSize(1);
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
		assertThat(scheduleInDay.get(0).getCalendar()).isNotNull();
	}

	@Test
	@DisplayName("schedule에서 scheduleAlarm 조회 시 lazy loading되어 조회된다.")
	void findScheduleWithFetchLazyScheduleAlarms() {
		ScheduleGroup scheduleGroup = em.persist(new ScheduleGroup(false));
		ScheduleAddRequest scheduleAddRequest =
				new ScheduleAddRequest(
						"title",
						"description",
						false,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 2, 1, 0, 0),
						false,
						null,
						"aaffbb",
						"서울역",
						true,
						36.0,
						127.0,
						true,
						List.of(LocalDateTime.of(2024, 1, 1, 0, 0)));
		Schedule savedSchedule =
				scheduleRepository.save(scheduleAddRequest.toSchedule(calendar, scheduleGroup));

		em.clear();
		Optional<Schedule> foundSchedule =
				scheduleRepository.findByScheduleId(savedSchedule.getId());

		assertThat(foundSchedule).isNotEmpty();
		assertThat(foundSchedule.get().getScheduleAlarms()).hasSize(1);
		assertThat(foundSchedule.get().getScheduleAlarms().get(0).getAlarmTime())
				.isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0));
	}

	@Test
	@DisplayName("일정을 삭제하면 조회되지 않는다.")
	void notSelectedWhenScheduleDeleted() {
		ScheduleGroup scheduleGroup = em.persist(new ScheduleGroup(false));
		Schedule savedSchedule =
				em.persist(ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar));

		savedSchedule.delete();

		em.flush();
		em.clear();
		Optional<Schedule> foundSchedule =
				scheduleRepository.findByScheduleId(savedSchedule.getId());

		assertThat(foundSchedule).isEmpty();
	}

	@Test
	@DisplayName("그룹 내 일정을 삭제하면 조회되지 않는다.")
	void notSelectedWhenSchedulesDeletedInGroup() {
		ScheduleGroup scheduleGroup = em.persist(new ScheduleGroup(false));
		em.persist(ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar));
		em.persist(ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar));
		em.persist(ScheduleFixture.DEFAULT.getSchedule(scheduleGroup, calendar));
		scheduleRepository.findGroupSchedules(scheduleGroup.getId()).forEach(Schedule::delete);

		em.flush();
		em.clear();
		List<Schedule> foundSchedule = scheduleRepository.findGroupSchedules(scheduleGroup.getId());

		assertThat(foundSchedule).hasSize(0);
	}

	@Test
	@DisplayName("그룹 내 특정 일 이후 일정을 삭제하면 모두 조회되지 않는다.")
	void notSelectedWhenSchedulesDeletedInGroupAfter() {
		ScheduleGroup scheduleGroup = em.persist(new ScheduleGroup(false));
		em.persist(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2024, 1, 1, 0, 0),
						LocalDateTime.of(2024, 1, 4, 0, 0)));
		Schedule schedule =
				em.persist(
						ScheduleFixture.DEFAULT.getSchedule(
								scheduleGroup,
								calendar,
								LocalDateTime.of(2024, 1, 3, 0, 0),
								LocalDateTime.of(2024, 1, 5, 0, 0)));
		em.persist(
				ScheduleFixture.DEFAULT.getSchedule(
						scheduleGroup,
						calendar,
						LocalDateTime.of(2024, 1, 7, 0, 0),
						LocalDateTime.of(2024, 1, 8, 0, 0)));
		scheduleRepository
				.findGroupSchedulesAfterCurrent(scheduleGroup.getId(), schedule.getStartDateTime())
				.forEach(Schedule::delete);

		em.flush();
		em.clear();
		List<Schedule> foundSchedule = scheduleRepository.findGroupSchedules(scheduleGroup.getId());

		assertThat(foundSchedule).hasSize(1);
	}
}
