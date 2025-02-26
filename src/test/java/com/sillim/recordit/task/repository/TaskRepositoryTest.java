package com.sillim.recordit.task.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.fixture.TaskFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
class TaskRepositoryTest {

	@Autowired TaskRepository taskRepository;
	@Autowired TestEntityManager em;

	private Member member;
	private CalendarCategory calendarCategory;
	private ScheduleCategory taskCategory;
	private Calendar calendar;
	private TaskGroup taskGroup;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		em.persist(member);
		taskCategory = em.persist(ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member));
		calendarCategory = em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(member));
		calendar = CalendarFixture.DEFAULT.getCalendar(member, calendarCategory);
		em.persist(calendar);
		taskGroup = new TaskGroup(null, null);
		em.persist(taskGroup);
	}

	@Test
	@DisplayName("새로운 할 일 레코드를 저장한다.")
	void saveTest() {
		final Task expected = TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup);
		Task saved =
				taskRepository.save(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));

		assertThat(saved)
				.usingRecursiveComparison()
				.ignoringFields("id", "calendar", "taskGroup", "createdAt", "modifiedAt")
				.isEqualTo(expected);
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 날짜의 할 일 레코드를 모두 조회한다.")
	void findAllByCalendarAndDateTest() {
		final List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 11), taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 13), taskCategory, calendar, taskGroup));
		taskRepository.saveAll(saved);

		List<Task> found =
				taskRepository.findAllByCalendarIdAndDate(
						calendar.getId(), LocalDate.of(2024, 6, 12));

		assertThat(found).hasSize(2);
		found.forEach(task -> assertThat(task.getDate()).isEqualTo(LocalDate.of(2024, 6, 12)));
	}

	@Test
	@DisplayName("캘린더 id와 할 일 그룹 id에 해당하는 할 일 목록을 조회한다.")
	void findAllByCalendarIdAndTaskGroupId() {
		List<Task> expected =
				List.of(
						TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		taskRepository.saveAll(expected);

		List<Task> found = taskRepository.findAllByTaskGroupId(calendar.getId(), taskGroup.getId());

		assertThat(found).hasSize(3);
		assertAll(
				() -> {
					assertThat(found.get(0))
							.usingRecursiveComparison()
							.ignoringFields("createdAt", "modifiedAt")
							.isEqualTo(expected.get(0));
					assertThat(found.get(1))
							.usingRecursiveComparison()
							.ignoringFields("createdAt", "modifiedAt")
							.isEqualTo(expected.get(1));
					assertThat(found.get(2))
							.usingRecursiveComparison()
							.ignoringFields("createdAt", "modifiedAt")
							.isEqualTo(expected.get(2));
				});
	}
}
