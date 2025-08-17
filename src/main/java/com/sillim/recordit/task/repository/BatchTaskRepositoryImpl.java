package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.Task;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BatchTaskRepositoryImpl implements BatchTaskRepository {

	private static final int BATCH_SIZE = 1000;
	private static final String SQL =
			"""
					INSERT INTO TASK (title, description, date, achieved, task_category_id, calendar_id, task_group_id, deleted, created_at, modified_at)
					VALUES (?,?,?,?,?,?,?,?,?,?)
					""";
	private final JdbcTemplate jdbcTemplate;

	@Override
	public void saveAllBatch(List<Task> tasks) {

		LocalDateTime now = LocalDateTime.now();
		for (int i = 0; i < tasks.size(); i += BATCH_SIZE) {
			List<Task> batch = tasks.subList(i, Math.min(i + BATCH_SIZE, tasks.size()));
			executeBatch(batch, now);
		}
	}

	private void executeBatch(List<Task> batch, LocalDateTime timestamp) {
		jdbcTemplate.batchUpdate(
				SQL,
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						Task task = batch.get(i);
						ps.setString(1, task.getTitle());
						ps.setString(2, task.getDescription());
						ps.setObject(3, task.getDate());
						ps.setBoolean(4, task.isAchieved());
						ps.setLong(5, task.getCategory().getId());
						ps.setLong(6, task.getCalendar().getId());
						ps.setLong(7, task.getTaskGroup().getId());
						ps.setBoolean(8, task.isDeleted());
						ps.setObject(9, timestamp);
						ps.setObject(10, timestamp);
					}

					@Override
					public int getBatchSize() {
						return batch.size();
					}
				});
	}
}
