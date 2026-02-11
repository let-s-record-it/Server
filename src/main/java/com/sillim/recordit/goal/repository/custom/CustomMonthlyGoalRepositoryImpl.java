package com.sillim.recordit.goal.repository.custom;

import static com.sillim.recordit.goal.domain.QMonthlyGoal.monthlyGoal;

import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CustomMonthlyGoalRepositoryImpl extends QuerydslRepositorySupport
		implements CustomMonthlyGoalRepository {

	public CustomMonthlyGoalRepositoryImpl() {
		super(MonthlyGoal.class);
	}

	@Override
	public Optional<MonthlyGoal> findMonthlyGoal(Long monthlyGoalId) {
		return Optional.ofNullable(
				selectFrom(monthlyGoal)
						.leftJoin(monthlyGoal.category)
						.fetchJoin()
						.leftJoin(monthlyGoal.calendar)
						.fetchJoin()
						.where(monthlyGoal.deleted.isFalse(), monthlyGoal.id.eq(monthlyGoalId))
						.fetchOne());
	}

	@Override
	public List<MonthlyGoal> findMonthlyGoalInMonth(Integer year, Integer month, Long calendarId) {
		return selectFrom(monthlyGoal)
				.leftJoin(monthlyGoal.category)
				.fetchJoin()
				.leftJoin(monthlyGoal.calendar)
				.fetchJoin()
				.where(
						monthlyGoal.deleted.isFalse(),
						monthlyGoal.calendar.id.eq(calendarId),
						monthlyGoal.period.startDate.year().eq(year),
						monthlyGoal.period.startDate.month().eq(month))
				.fetch();
	}
}
