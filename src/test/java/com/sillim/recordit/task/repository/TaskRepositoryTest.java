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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
class TaskRepositoryTest {

	@Autowired TaskRepository taskRepository;
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
	@DisplayName("새로운 할 일 레코드를 저장한다.")
	void saveTest() {
		final Task expected = TaskFixture.DEFAULT.get(calendar, taskGroup);
		Task saved = taskRepository.save(TaskFixture.DEFAULT.get(calendar, taskGroup));

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
								LocalDate.of(2024, 6, 11), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 13), calendar, taskGroup));
		taskRepository.saveAll(saved);

		List<Task> found =
				taskRepository.findAllByCalendarIdAndDate(
						calendar.getId(), LocalDate.of(2024, 6, 12));

		assertThat(found).hasSize(2);
		found.forEach(task -> assertThat(task.getDate()).isEqualTo(LocalDate.of(2024, 6, 12)));
	}

	@Test
	@DisplayName("해당 캘린더에 속하는 특정 id의 할 일을 조회한다.")
	void findByIdAndCalendarId() {

		Task saved = TaskFixture.DEFAULT.get(calendar, taskGroup);
		taskRepository.save(saved);

		Optional<Task> found =
				taskRepository.findByIdAndCalendarId(saved.getId(), calendar.getId());

		assertThat(found).isNotEmpty();
		assertThat(found.get().getId()).isEqualTo(saved.getId());
	}

	@Test
	@DisplayName("해당 할 일 그룹에 속한 할 일 중, 선택한 할 일을 제외하고 모두 삭제한다.")
	void deleteAllByTaskGroupIdAndTaskIdNot() {
		final List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup));
		taskRepository.saveAll(saved);
		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		taskRepository.save(selectedTask);

		taskRepository.deleteAllByTaskGroupIdAndTaskIdNot(taskGroup.getId(), selectedTask.getId());

		assertThat(taskRepository.findById(selectedTask.getId())).isNotEmpty();
		saved.forEach(
				task -> {
					assertThat(taskRepository.findById(task.getId())).isEmpty();
				});
	}

	@Test
	@DisplayName("해당 할 일 그룹에 속한 달성하지 못한 할 일 중, 선택한 할 일을 제외하고 모두 삭제한다.")
	void deleteAllNotAchievedTasksByTaskGroupIdAndTaskIdNot() {
		final List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup));
		taskRepository.saveAll(saved);
		Task selectedTask = TaskFixture.DEFAULT.get(calendar, taskGroup);
		taskRepository.save(selectedTask);

		taskRepository.deleteAllNotAchievedTasksByTaskGroupIdAndTaskIdNot(
				taskGroup.getId(), selectedTask.getId());

		assertThat(taskRepository.findById(selectedTask.getId())).isNotEmpty();
		saved.forEach(
				task -> {
					assertThat(taskRepository.findById(task.getId())).isEmpty();
				});
	}

	@Test
	@DisplayName("캘린더 id와 할 일 그룹 id에 해당하는 할 일 목록을 조회한다.")
	void findAllByCalendarIdAndTaskGroupId() {
		List<Task> expected =
				List.of(
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup),
						TaskFixture.DEFAULT.get(calendar, taskGroup));
		taskRepository.saveAll(expected);

		List<Task> found =
				taskRepository.findAllByCalendarIdAndTaskGroupId(
						calendar.getId(), taskGroup.getId());

		assertThat(found).hasSize(3);
		assertAll(
				() -> {
					assertThat(found.get(0))
							.usingRecursiveComparison()
							.ignoringFields("id", "createdAt", "modifiedAt")
							.isEqualTo(expected.get(0));
					assertThat(found.get(1))
							.usingRecursiveComparison()
							.ignoringFields("id", "createdAt", "modifiedAt")
							.isEqualTo(expected.get(1));
					assertThat(found.get(2))
							.usingRecursiveComparison()
							.ignoringFields("id", "createdAt", "modifiedAt")
							.isEqualTo(expected.get(2));
				});
	}

	@Test
	@DisplayName("할 일 그룹 id에 해당하는 할 일 중, date 컬럼이 같은 레코드가 있는지 여부를 반환한다.")
	void existsByTaskGroupIdAndDate() {
		List<Task> saved =
				List.of(
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 11), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 12), calendar, taskGroup),
						TaskFixture.DEFAULT.getWithDate(
								LocalDate.of(2024, 6, 13), calendar, taskGroup));
		taskRepository.saveAll(saved);

		boolean shouldBeTrue1 =
				taskRepository.existsByTaskGroupIdAndDate(
						taskGroup.getId(), LocalDate.of(2024, 6, 11));
		boolean shouldBeTrue2 =
				taskRepository.existsByTaskGroupIdAndDate(
						taskGroup.getId(), LocalDate.of(2024, 6, 12));
		boolean shouldBeTrue3 =
				taskRepository.existsByTaskGroupIdAndDate(
						taskGroup.getId(), LocalDate.of(2024, 6, 13));
		boolean shouldBeFalse1 =
				taskRepository.existsByTaskGroupIdAndDate(
						taskGroup.getId(), LocalDate.of(2024, 6, 10));
		boolean shouldBeFalse2 =
				taskRepository.existsByTaskGroupIdAndDate(
						taskGroup.getId(), LocalDate.of(2024, 6, 14));

		assertAll(
				() -> {
					assertThat(shouldBeTrue1).isTrue();
					assertThat(shouldBeTrue2).isTrue();
					assertThat(shouldBeTrue3).isTrue();
					assertThat(shouldBeFalse1).isFalse();
					assertThat(shouldBeFalse2).isFalse();
				});
	}
}
