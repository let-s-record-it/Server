package com.sillim.recordit.schedule.repository;

import static com.sillim.recordit.schedule.domain.QSchedule.schedule;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.schedule.domain.Schedule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomScheduleRepositoryImpl extends QuerydslRepositorySupport
		implements CustomScheduleRepository {

	public CustomScheduleRepositoryImpl() {
		super(Schedule.class);
	}

	@Override
	public Optional<Schedule> findByScheduleId(Long scheduleId) {
		return Optional.ofNullable(
				selectFrom(schedule)
						.leftJoin(schedule.calendar)
						.fetchJoin()
						.leftJoin(schedule.calendar.member)
						.fetchJoin()
						.leftJoin(schedule.scheduleGroup)
						.fetchJoin()
						.where(schedule.id.eq(scheduleId).and(schedule.deleted.isFalse()))
						.fetchOne());
	}

	@Override
	public List<Schedule> findScheduleInMonth(Long calendarId, Integer year, Integer month) {
		return selectFrom(schedule)
				.where(schedule.calendar.id.eq(calendarId).and(schedule.deleted.isFalse()))
				.where(startLtYear(year).or(startEqYear(year).and(StartLoeMonth(month))))
				.where(endGtYear(year).or(endEqYear(year).and(endGoeMonth(month))))
				.orderBy(schedule.scheduleDuration.startDateTime.asc())
				.fetch();
	}

	@Override
	public List<Schedule> findScheduleInDay(Long calendarId, LocalDate date) {
		return selectFrom(schedule)
				.leftJoin(schedule.calendar)
				.fetchJoin()
				.leftJoin(schedule.scheduleGroup)
				.fetchJoin()
				.leftJoin(schedule.scheduleGroup.repetitionPattern)
				.fetchJoin()
				.where(schedule.calendar.id.eq(calendarId).and(schedule.deleted.isFalse()))
				.where(schedule.scheduleDuration.startDateTime.loe(date.atStartOfDay()))
				.where(schedule.scheduleDuration.endDateTime.goe(date.atStartOfDay()))
				.fetch();
	}

	@Override
	public List<Schedule> findGroupSchedules(Long scheduleGroupId) {
		return selectFrom(schedule)
				.where(
						schedule.scheduleGroup
								.id
								.eq(scheduleGroupId)
								.and(schedule.deleted.isFalse()))
				.fetch();
	}

	public List<Schedule> findGroupSchedulesAfterCurrent(
			Long scheduleGroupId, LocalDateTime dateTime) {
		return selectFrom(schedule)
				.where(
						schedule.scheduleGroup
								.id
								.eq(scheduleGroupId)
								.and(schedule.deleted.isFalse()))
				.where(schedule.scheduleDuration.startDateTime.goe(dateTime))
				.fetch();
	}

	@Override
	public List<Schedule> findScheduleMatchedQuery(String query, Long calendarId) {
		return selectFrom(schedule)
				.leftJoin(schedule.calendar)
				.fetchJoin()
				.leftJoin(schedule.scheduleGroup)
				.fetchJoin()
				.leftJoin(schedule.scheduleGroup.repetitionPattern)
				.fetchJoin()
				.where(schedule.deleted.isFalse())
				.where(schedule.calendar.id.eq(calendarId))
				.where(schedule.title.title.contains(query))
				.fetch();
	}

	private static BooleanExpression endGoeMonth(Integer month) {
		return schedule.scheduleDuration.endDateTime.month().goe(month);
	}

	private static BooleanExpression endGtYear(Integer year) {
		return schedule.scheduleDuration.endDateTime.year().gt(year);
	}

	private static BooleanExpression StartLoeMonth(Integer month) {
		return schedule.scheduleDuration.startDateTime.month().loe(month);
	}

	private static BooleanExpression startEqYear(Integer year) {
		return schedule.scheduleDuration.startDateTime.year().eq(year);
	}

	private static BooleanExpression endEqYear(Integer year) {
		return schedule.scheduleDuration.endDateTime.year().eq(year);
	}

	private static BooleanExpression startLtYear(Integer year) {
		return schedule.scheduleDuration.startDateTime.year().lt(year);
	}
}
