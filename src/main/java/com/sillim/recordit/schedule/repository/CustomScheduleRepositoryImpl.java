package com.sillim.recordit.schedule.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.schedule.domain.QSchedule;
import com.sillim.recordit.schedule.domain.Schedule;
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
		return selectFrom(QSchedule.schedule)
				.where(QSchedule.schedule.calendar.id.eq(calendarId))
				.where(startLtYear(year).or(startEqYear(year).and(StartLoeMonth(month))))
				.where(endGtYear(year).or(endEqYear(year).and(endGoeMonth(month))))
				.orderBy(QSchedule.schedule.scheduleDuration.startDatetime.asc())
				.fetch();
	}

	private static BooleanExpression endGoeMonth(Integer month) {
		return QSchedule.schedule.scheduleDuration.endDatetime.month().goe(month);
	}

	private static BooleanExpression endGtYear(Integer year) {
		return QSchedule.schedule.scheduleDuration.endDatetime.year().gt(year);
	}

	private static BooleanExpression StartLoeMonth(Integer month) {
		return QSchedule.schedule.scheduleDuration.startDatetime.month().loe(month);
	}

	private static BooleanExpression startEqYear(Integer year) {
		return QSchedule.schedule.scheduleDuration.startDatetime.year().eq(year);
	}

	private static BooleanExpression endEqYear(Integer year) {
		return QSchedule.schedule.scheduleDuration.endDatetime.year().eq(year);
	}

	private static BooleanExpression startLtYear(Integer year) {
		return QSchedule.schedule.scheduleDuration.startDatetime.year().lt(year);
	}
}
