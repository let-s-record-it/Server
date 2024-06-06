package com.sillim.recordit.schedule.repository;

import static com.sillim.recordit.schedule.domain.QSchedule.schedule;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CustomScheduleRepositoryImpl extends QuerydslRepositorySupport
		implements CustomScheduleRepository {

	public CustomScheduleRepositoryImpl() {
		super(Schedule.class);
	}

	@Override
	public List<Schedule> findScheduleInMonth(Long calendarId, Integer year, Integer month) {
		return selectFrom(schedule)
				.where(schedule.calendar.id.eq(calendarId))
				.where(startLtYear(year).or(startEqYear(year).and(StartLoeMonth(month))))
				.where(endGtYear(year).or(endEqYear(year).and(endGoeMonth(month))))
				.orderBy(schedule.scheduleDuration.startDatetime.asc())
				.fetch();
	}

	@Override
	public List<Schedule> findScheduleInDay(Long calendarId, LocalDate date) {
		return selectFrom(schedule)
				.where(schedule.calendar.id.eq(calendarId))
				.where(schedule.scheduleDuration.startDatetime.loe(date.atStartOfDay()))
				.where(schedule.scheduleDuration.endDatetime.goe(date.atStartOfDay()))
				.leftJoin(schedule.calendar)
				.fetchJoin()
				.leftJoin(schedule.scheduleGroup)
				.fetchJoin()
				.leftJoin(schedule.scheduleGroup.repetitionPattern)
				.fetchJoin()
				.fetch();
	}

	private static BooleanExpression endGoeMonth(Integer month) {
		return schedule.scheduleDuration.endDatetime.month().goe(month);
	}

	private static BooleanExpression endGtYear(Integer year) {
		return schedule.scheduleDuration.endDatetime.year().gt(year);
	}

	private static BooleanExpression StartLoeMonth(Integer month) {
		return schedule.scheduleDuration.startDatetime.month().loe(month);
	}

	private static BooleanExpression startEqYear(Integer year) {
		return schedule.scheduleDuration.startDatetime.year().eq(year);
	}

	private static BooleanExpression endEqYear(Integer year) {
		return schedule.scheduleDuration.endDatetime.year().eq(year);
	}

	private static BooleanExpression startLtYear(Integer year) {
		return schedule.scheduleDuration.startDatetime.year().lt(year);
	}
}
