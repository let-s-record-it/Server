package com.sillim.recordit.task.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import com.sillim.recordit.task.fixture.TaskRepetitionPatternFixture;
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
class TaskRepetitionPatternRepositoryTest {

	@Autowired TaskRepetitionPatternRepository taskRepetitionPatternRepository;
	@Autowired TestEntityManager em;

	private TaskGroup taskGroup;

	@BeforeEach
	void init() {
		taskGroup = new TaskGroup(true, null, null);
		em.persist(taskGroup);
	}

	@Test
	@DisplayName("새로운 반복 패턴을 저장한다.")
	void saveTest() {
		final TaskRepetitionPattern expected = TaskRepetitionPatternFixture.DAILY.get(taskGroup);
		TaskRepetitionPattern saved =
				taskRepetitionPatternRepository.save(
						TaskRepetitionPatternFixture.DAILY.get(taskGroup));

		assertThat(saved)
				.usingRecursiveComparison()
				.ignoringFields("id", "taskGroup", "createdAt", "modifiedAt")
				.isEqualTo(expected);
	}

	@Test
	@DisplayName("할 일 그룹의 id로 반복 패턴을 조회한다.")
	void findByTaskGroupId() {
		TaskRepetitionPattern expected = TaskRepetitionPatternFixture.DAILY.get(taskGroup);
		taskRepetitionPatternRepository.save(TaskRepetitionPatternFixture.DAILY.get(taskGroup));

		Optional<TaskRepetitionPattern> found =
				taskRepetitionPatternRepository.findByTaskGroupId(taskGroup.getId());

		assertThat(found).isNotEmpty();
		assertThat(found.get())
				.usingRecursiveComparison()
				.ignoringFields("id", "taskGroup", "createdAt", "modifiedAt")
				.isEqualTo(expected);
	}
}
