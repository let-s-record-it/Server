package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyGoalRepository
		extends JpaRepository<MonthlyGoal, Long>, CustomMonthlyGoalRepository {

	Optional<MonthlyGoal> findByIdAndMember(Long monthlyGoalId, Member member);

	void deleteByIdAndMember(Long monthlyGoalId, Member member);
}
