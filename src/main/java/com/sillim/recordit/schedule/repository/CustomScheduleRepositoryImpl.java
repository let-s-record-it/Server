package com.sillim.recordit.schedule.repository;

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
		QSchedule schedule = QSchedule.schedule;
		return selectFrom(schedule)
				.where(schedule.calendar.id.eq(calendarId))
				.where(
						schedule.scheduleDuration
								.startDatetime
								.year()
								.lt(year)
								.or(
										schedule.scheduleDuration
												.startDatetime
												.year()
												.eq(year)
												.and(
														schedule.scheduleDuration
																.startDatetime
																.month()
																.loe(month)))
								.and(
										schedule.scheduleDuration
												.endDatetime
												.year()
												.gt(year)
												.or(
														schedule.scheduleDuration
																.endDatetime
																.year()
																.eq(year)
																.and(
																		schedule.scheduleDuration
																				.endDatetime
																				.month()
																				.goe(month)))))
				.fetch();
	}
}
