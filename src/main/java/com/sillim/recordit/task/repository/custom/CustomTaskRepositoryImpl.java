package com.sillim.recordit.task.repository.custom;

import static com.sillim.recordit.task.domain.QTask.task;
import static com.sillim.recordit.task.domain.QTaskGroup.taskGroup;

import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomTaskRepositoryImpl extends QuerydslRepositorySupport
		implements CustomTaskRepository {

	public CustomTaskRepositoryImpl() {
		super(Task.class);
	}

	@Override
	public Optional<Task> findByIdAndCalendarId(Long taskId, Long calendarId) {
		return Optional.ofNullable(
				selectFrom(task)
						.leftJoin(task.calendar)
						.fetchJoin()
						.leftJoin(task.taskGroup)
						.fetchJoin()
						.leftJoin(taskGroup.monthlyGoal)
						.fetchJoin()
						.leftJoin(taskGroup.weeklyGoal)
						.fetchJoin()
						.leftJoin(task.category)
						.fetchJoin()
						.where(
								task.deleted.isFalse(),
								task.id.eq(taskId).and(task.calendar.id.eq(calendarId)))
						.fetchOne());
	}

	@Override
	public List<Task> findTasksInMonth(Long calendarId, Integer year, Integer month) {
		return selectFrom(task)
				.leftJoin(task.category)
				.fetchJoin()
				.where(
						task.deleted.isFalse(),
						task.calendar.id.eq(calendarId),
						task.date.year().eq(year).and(task.date.month().eq(month)))
				.fetch();
	}

	@Override
	public void deleteAllByTaskGroupId(Long taskGroupId) {
		getEntityManager().flush();

		update(task)
				.set(task.deleted, true)
				.where(task.deleted.isFalse(), task.taskGroup.id.eq(taskGroupId))
				.execute();

		getEntityManager().clear();
	}

	@Override
	public void deleteAllByTaskGroupIdAndDateAfterOrEqual(Long taskGroupId, LocalDate date) {
		getEntityManager().flush();

		update(task)
				.set(task.deleted, true)
				.where(
						task.deleted.isFalse(),
						task.taskGroup.id.eq(taskGroupId),
						task.date.goe(date))
				.execute();

		getEntityManager().clear();
	}
}
