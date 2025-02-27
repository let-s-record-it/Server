package com.sillim.recordit.goal.repository;

import static com.sillim.recordit.goal.domain.QWeeklyGoal.weeklyGoal;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomWeeklyGoalRepositoryImpl extends QuerydslRepositorySupport
		implements CustomWeeklyGoalRepository {

	public CustomWeeklyGoalRepositoryImpl() {
		super(MonthlyGoal.class);
	}

	@Override
	public List<WeeklyGoal> findWeeklyGoalInMonth(Integer year, Integer month, Long calendarId) {
		return selectFrom(weeklyGoal)
				.leftJoin(weeklyGoal.relatedMonthlyGoal)
				.fetchJoin()
				.leftJoin(weeklyGoal.category)
				.fetchJoin()
				.where(weeklyGoal.calendar.id.eq(calendarId))
				.where(containStartDateOrEndDate(year, month))
				.where(notNextMonth(month))
				.where(notPrevMonth(month))
				.fetch();
	}

	private static BooleanExpression containStartDateOrEndDate(Integer year, Integer month) {
		return weeklyGoal
				.period
				.startDate
				.year()
				.eq(year)
				.and(weeklyGoal.period.startDate.month().eq(month))
				.or(
						weeklyGoal
								.period
								.endDate
								.year()
								.eq(year)
								.and(weeklyGoal.period.endDate.month().eq(month)));
	}

	private BooleanExpression notNextMonth(Integer month) {
		return (weeklyGoal.period.endDate.month().ne(month).and(weeklyGoal.period.week.eq(1)))
				.not();
	}

	private BooleanExpression notPrevMonth(Integer month) {
		return (weeklyGoal.period.startDate.month().ne(month).and(weeklyGoal.period.week.ne(1)))
				.not();
	}

	@Override
	public Optional<WeeklyGoal> findWeeklyGoalById(Long id) {
		return Optional.ofNullable(
				selectFrom(weeklyGoal)
						.leftJoin(weeklyGoal.relatedMonthlyGoal)
						.fetchJoin()
						.leftJoin(weeklyGoal.category)
						.fetchJoin()
						.where(weeklyGoal.id.eq(id))
						.fetchOne());
	}
}
