package com.sillim.recordit.task.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class BatchTaskRepositoryTest {

	private static final String EXPECTED_SQL =
			"""
					INSERT INTO TASK (title, description, date, achieved, task_category_id, calendar_id, task_group_id, deleted, created_at, modified_at)
					VALUES (?,?,?,?,?,?,?,?,?,?)
					""";

	BatchTaskRepository batchTaskRepository;
	@Mock JdbcTemplate jdbcTemplate;

	private Member member;
	private CalendarCategory calendarCategory;
	private ScheduleCategory taskCategory;
	private Calendar calendar;
	private TaskGroup taskGroup;

	@BeforeEach
	void init() {
		member = MemberFixture.DEFAULT.getMember();
		calendarCategory = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		calendar = CalendarFixture.DEFAULT.getCalendar(member, calendarCategory);
		taskGroup = new TaskGroup(null, null);
		taskCategory = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		batchTaskRepository = new BatchTaskRepositoryImpl(jdbcTemplate);
	}

	@Test
	@DisplayName("1000개 이하의 Task를 저장하는 경우, 한 번의 Batch로 저장된다.")
	void saveAllBatch_ExecutesSingleBatch() {
		ArgumentCaptor<BatchPreparedStatementSetter> captor =
				ArgumentCaptor.forClass(BatchPreparedStatementSetter.class);
		final List<Task> tasks = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			tasks.add(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		}
		batchTaskRepository.saveAllBatch(tasks);

		then(jdbcTemplate).should(times(1)).batchUpdate(eq(EXPECTED_SQL), captor.capture());
		List<BatchPreparedStatementSetter> setters = captor.getAllValues();
		assertThat(setters).hasSize(1);
		assertThat(setters.get(0).getBatchSize()).isEqualTo(1000);
	}

	@Test
	@DisplayName("1000개를 초과하는 Task를 저장하는 경우, 여러 번의 Batch로 분할하여 저장한다.")
	void saveAllBatch_ExecutesMultipleBatches() {
		ArgumentCaptor<BatchPreparedStatementSetter> captor =
				ArgumentCaptor.forClass(BatchPreparedStatementSetter.class);
		final List<Task> tasks = new ArrayList<>();
		for (int i = 0; i < 1500; i++) {
			tasks.add(TaskFixture.DEFAULT.get(taskCategory, calendar, taskGroup));
		}
		batchTaskRepository.saveAllBatch(tasks);

		then(jdbcTemplate).should(times(2)).batchUpdate(eq(EXPECTED_SQL), captor.capture());
		List<BatchPreparedStatementSetter> setters = captor.getAllValues();
		assertThat(setters).hasSize(2);
		assertThat(setters.get(0).getBatchSize()).isEqualTo(1000);
		assertThat(setters.get(1).getBatchSize()).isEqualTo(500);
	}
}
