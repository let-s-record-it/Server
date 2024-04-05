package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.Member;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyGoalJpaRepository extends JpaRepository<MonthlyGoal, Long> {

	List<MonthlyGoal> findByGoalYearAndGoalMonthAndMember(
			Integer goalYear, Integer goalMonth, Member member);
}
