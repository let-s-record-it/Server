package com.sillim.recordit.task.repository;

import static com.sillim.recordit.calendar.domain.QCalendar.calendar;
import static com.sillim.recordit.goal.domain.QMonthlyGoal.monthlyGoal;
import static com.sillim.recordit.goal.domain.QWeeklyGoal.weeklyGoal;
import static com.sillim.recordit.task.domain.QTask.task;
import static com.sillim.recordit.task.domain.QTaskGroup.taskGroup;

import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
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
						.leftJoin(task.calendar, calendar)
						.fetchJoin()
						.leftJoin(task.taskGroup, taskGroup)
						.fetchJoin()
						.leftJoin(taskGroup.monthlyGoal, monthlyGoal)
						.fetchJoin()
						.leftJoin(taskGroup.weeklyGoal, weeklyGoal)
						.fetchJoin()
						.where(task.id.eq(taskId).and(task.calendar.id.eq(calendarId)))
						.fetchOne());
	}

	@Override
	public void deleteAllByTaskGroupIdAndTaskIdNot(Long taskGroupId, Long taskId) {

		getEntityManager().flush();
		update(task)
				.set(task.deleted, true)
				.where(task.taskGroup.id.eq(taskGroupId).and(task.id.ne(taskId)))
				.execute();
		getEntityManager().clear();
	}

	@Override
	public void deleteAllNotAchievedTasksByTaskGroupIdAndTaskIdNot(Long taskGroupId, Long taskId) {
		getEntityManager().flush();
		update(task)
				.set(task.deleted, true)
				.where(
						task.taskGroup
								.id
								.eq(taskGroupId)
								.and(task.achieved.eq(false))
								.and(task.id.ne(taskId)))
				.execute();
		getEntityManager().clear();
	}

	@Override
	public void deleteAllByTaskGroupIdAndDateAfter(Long taskGroupId, LocalDate date) {
		getEntityManager().flush();
		update(task)
				.set(task.deleted, true)
				.where(task.taskGroup.id.eq(taskGroupId).and(task.date.after(date)));
		getEntityManager().clear();
	}

	@Override
	public void deleteAllNotAchievedByTaskGroupIdAndDateAfter(Long taskGroupId, LocalDate date) {
		getEntityManager().flush();
		update(task)
				.set(task.deleted, true)
				.where(
						task.taskGroup
								.id
								.eq(taskGroupId)
								.and(task.achieved.eq(false))
								.and(task.date.after(date)));
		getEntityManager().clear();
	}
}
