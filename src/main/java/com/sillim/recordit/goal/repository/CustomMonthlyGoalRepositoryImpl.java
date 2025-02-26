package com.sillim.recordit.goal.repository;

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
	public Optional<MonthlyGoal> findByIdWithFetch(Long monthlyGoalId) {
		return Optional.ofNullable(
				selectFrom(monthlyGoal)
						.leftJoin(monthlyGoal.category)
						.fetchJoin()
						.where(monthlyGoal.id.eq(monthlyGoalId))
						.fetchOne());
	}

	@Override
	public List<MonthlyGoal> findMonthlyGoalInMonth(
			Integer year, Integer month, Long memberId, Long calendarId) {
		return selectFrom(monthlyGoal)
				.leftJoin(monthlyGoal.category)
				.fetchJoin()
				.where(monthlyGoal.calendar.id.eq(calendarId))
				.where(
						monthlyGoal
								.member
								.id
								.eq(memberId)
								.and(monthlyGoal.period.startDate.year().eq(year))
								.and(monthlyGoal.period.startDate.month().eq(month)))
				.fetch();
	}
}
