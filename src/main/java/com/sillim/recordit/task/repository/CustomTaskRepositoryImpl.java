package com.sillim.recordit.task.repository;

import static com.sillim.recordit.calendar.domain.QCalendar.calendar;
import static com.sillim.recordit.goal.domain.QMonthlyGoal.monthlyGoal;
import static com.sillim.recordit.goal.domain.QWeeklyGoal.weeklyGoal;
import static com.sillim.recordit.task.domain.QTask.task;
import static com.sillim.recordit.task.domain.QTaskGroup.taskGroup;

import com.sillim.recordit.calendar.domain.Calendar;
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
	public void updateAllByCalendarIdAndTaskGroupId(
			Long calendarId,
			Long taskGroupId,
			String newTitle,
			String newDescription,
			LocalDate newDate,
			String newColorHex,
			Calendar newCalendar) {

		getEntityManager().flush();
		update(task)
				.set(task.title.title, newTitle)
				.set(task.description.description, newDescription)
				.set(task.date, newDate)
				.set(task.colorHex.colorHex, newColorHex)
				.set(task.calendar, newCalendar)
				.where(task.calendar.id.eq(calendarId).and(task.taskGroup.id.eq(taskGroupId)))
				.execute();
		getEntityManager().clear();
	}
}
