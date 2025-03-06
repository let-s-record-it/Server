package com.sillim.recordit.task.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class CustomTaskRepositoryTest {

	@Qualifier("customTaskRepositoryImpl") @Autowired
	CustomTaskRepository taskRepository;

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
		calendarCategory = em.persist(CalendarCategoryFixture.DEFAULT.getCalendarCategory(member));
		calendar = CalendarFixture.DEFAULT.getCalendar(member, calendarCategory);
		em.persist(calendar);
		taskGroup = new TaskGroup(null, null);
		em.persist(taskGroup);
		taskCategory = em.persist(ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar));
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 id의 할 일을 조회한다.")
	void findByIdAndCalendarId() {

		Task saved = TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup);
		em.persist(saved);

		Optional<Task> found =
				taskRepository.findByIdAndCalendarId(saved.getId(), calendar.getId());

		assertThat(found).isNotEmpty();
		assertThat(found.get().getId()).isEqualTo(saved.getId());
	}

	@Test
	@DisplayName("해당 할 일 그룹에 속하는 모든 할 일을 삭제한다.")
	void deleteAllByTaskGroupId() {

		List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		saved.forEach(em::persist);

		taskRepository.deleteAllByTaskGroupId(taskGroup.getId());

		assertThat(em.find(Task.class, saved.get(0).getId())).isNull();
		assertThat(em.find(Task.class, saved.get(1).getId())).isNull();
	}

	@Test
	@DisplayName("해당 할 일 그룹에 속하는 모든 할 일 중 특정 날짜 이후의 할일을 삭제한다.")
	void deleteAllByTaskGroupIdAndDateAfterOrEqual() {

		List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 8, 12), taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 8, 13), taskCategory, calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 8, 14), taskCategory, calendar, taskGroup));
		saved.forEach(em::persist);

		taskRepository.deleteAllByTaskGroupIdAndDateAfterOrEqual(
				taskGroup.getId(), LocalDate.of(2024, 8, 13));

		assertThat(em.find(Task.class, saved.get(0).getId())).isNotNull();
		assertThat(em.find(Task.class, saved.get(1).getId())).isNull();
		assertThat(em.find(Task.class, saved.get(1).getId())).isNull();
	}
}
