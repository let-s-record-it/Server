package com.sillim.recordit.task.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.task.domain.Task;
import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.fixture.TaskFixture;
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
}
