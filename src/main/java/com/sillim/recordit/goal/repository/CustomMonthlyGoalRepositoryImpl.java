package com.sillim.recordit.goal.repository;

import static com.sillim.recordit.goal.domain.QMonthlyGoal.monthlyGoal;

import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CustomMonthlyGoalRepositoryImpl extends QuerydslRepositorySupport
		implements CustomMonthlyGoalRepository {

	public CustomMonthlyGoalRepositoryImpl() {
		super(MonthlyGoal.class);
	}

	@Override
	public List<MonthlyGoal> findMonthlyGoalInMonth(Integer year, Integer month, Member member) {
		return selectFrom(monthlyGoal)
				.where(
						monthlyGoal
								.period
								.startDate
								.year()
								.eq(year)
								.and(monthlyGoal.period.startDate.month().eq(month))
								.and(monthlyGoal.member.eq(member)))
				.fetch();
	}
}
