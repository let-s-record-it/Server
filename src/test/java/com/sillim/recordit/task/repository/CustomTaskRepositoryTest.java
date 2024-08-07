package com.sillim.recordit.task.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
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
	private Calendar calendar;
	private TaskGroup taskGroup;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		em.persist(member);
		calendar = CalendarFixture.DEFAULT.getCalendar(member);
		em.persist(calendar);
		taskGroup = new TaskGroup(false, null, null);
		em.persist(taskGroup);
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 id의 할 일을 조회한다.")
	void findByIdAndCalendarId() {

		Task saved = TaskFixture.DEFAULT.get(calendar, taskGroup);
		em.persist(saved);

		Optional<Task> found =
				taskRepository.findByIdAndCalendarId(saved.getId(), calendar.getId());

		assertThat(found).isNotEmpty();
		assertThat(found.get().getId()).isEqualTo(saved.getId());
	}

	@Test
	@DisplayName("해당 할 일 그룹에 속한 할 일 중, 선택한 할 일을 제외하고 모두 삭제한다.")
	void deleteAllByTaskGroupIdAndTaskIdNot() {
		List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup));
		saved.forEach(em::persist);
		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		em.persist(selectedTask);

		taskRepository.deleteAllByTaskGroupIdAndTaskIdNot(taskGroup.getId(), selectedTask.getId());

		assertAll(
				() -> {
					assertThat(em.find(Task.class, selectedTask.getId())).isNotNull();
					saved.forEach(
							task -> {
								assertThat(em.find(Task.class, task.getId())).isNull();
							});
				});
	}

	@Test
	@DisplayName("해당 할 일 그룹에 속한 달성하지 못한 할 일 중, 선택한 할 일을 제외하고 모두 삭제한다.")
	void deleteAllNotAchievedTasksByTaskGroupIdAndTaskIdNot() {
		List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup));
		saved.forEach(em::persist);
		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		em.persist(selectedTask);

		taskRepository.deleteAllNotAchievedTasksByTaskGroupIdAndTaskIdNot(
				taskGroup.getId(), selectedTask.getId());

		assertAll(
				() -> {
					assertThat(em.find(Task.class, selectedTask.getId())).isNotNull();
					saved.forEach(
							task -> {
								assertThat(em.find(Task.class, task.getId())).isNull();
							});
				});
	}

	@Test
	@DisplayName("해당 할 일 그룹에 속한 할 일 중, 선택한 할 일 이후의 날짜에 해당하는 할 일을 모두 삭제한다.")
	void deleteAllByTaskGroupIdAndDateAfter() {
		List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 7, 13), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 7, 15), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 7, 16), calendar, taskGroup));
		saved.forEach(em::persist);
		Task selectedTask =
				TaskFixture.DEFAULT.getWithDate(LocalDate.of(2024, 7, 14), calendar, taskGroup);
		em.persist(selectedTask);

		taskRepository.deleteAllByTaskGroupIdAndDateAfter(
				taskGroup.getId(), selectedTask.getDate());

		assertAll(
				() -> {
					assertThat(em.find(Task.class, selectedTask.getId())).isNotNull();
					assertThat(em.find(Task.class, saved.get(0).getId())).isNotNull();
					assertThat(em.find(Task.class, saved.get(1).getId())).isNull();
					assertThat(em.find(Task.class, saved.get(2).getId())).isNull();
				});
	}

	@Test
	@DisplayName("해당 할 일 그룹에 속한 달성하지 못한 할 일 중, 선택한 할 일 이후의 날짜에 해당하는 할 일을 모두 삭제한다.")
	void deleteAllNotAchievedTasksByTaskGroupIdAndDateAfter() {
		List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 7, 13), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 7, 15), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 7, 16), calendar, taskGroup));
		saved.forEach(em::persist);
		Task selectedTask =
				TaskFixture.DEFAULT.getWithDate(LocalDate.of(2024, 7, 14), calendar, taskGroup);
		em.persist(selectedTask);

		taskRepository.deleteAllNotAchievedByTaskGroupIdAndDateAfter(
				taskGroup.getId(), selectedTask.getDate());

		assertAll(
				() -> {
					assertThat(em.find(Task.class, selectedTask.getId())).isNotNull();
					assertThat(em.find(Task.class, saved.get(0).getId())).isNotNull();
					assertThat(em.find(Task.class, saved.get(1).getId())).isNull();
					assertThat(em.find(Task.class, saved.get(2).getId())).isNull();
				});
	}
}
