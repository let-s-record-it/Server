package com.sillim.recordit.goal.repository;

import static com.sillim.recordit.goal.domain.QWeeklyGoal.weeklyGoal;

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
	public List<WeeklyGoal> findWeeklyGoalInMonth(Integer year, Integer month, Long memberId) {
		return selectFrom(weeklyGoal)
				.leftJoin(weeklyGoal.relatedMonthlyGoal)
				.fetchJoin()
				.where(
						weeklyGoal
								.member
								.id
								.eq(memberId)
								.and(
										weeklyGoal
												.period
												.startDate
												.year()
												.eq(year)
												.and(weeklyGoal.period.startDate.month().eq(month)))
								.or(
										weeklyGoal
												.period
												.endDate
												.year()
												.eq(year)
												.and(weeklyGoal.period.endDate.month().eq(month))))
				.fetch();
	}

	@Override
	public Optional<WeeklyGoal> findWeeklyGoalById(Long id) {
		return Optional.ofNullable(
				selectFrom(weeklyGoal)
						.leftJoin(weeklyGoal.relatedMonthlyGoal)
						.fetchJoin()
						.where(weeklyGoal.id.eq(id))
						.fetchOne());
	}
}
