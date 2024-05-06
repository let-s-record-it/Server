package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyGoalJpaRepository extends JpaRepository<MonthlyGoal, Long> {

	Optional<MonthlyGoal> findByIdAndMember(Long monthlyGoalId, Member member);

	List<MonthlyGoal> findByPeriod_StartDateAndPeriod_EndDateAndMember(
			LocalDate startDate, LocalDate endDate, Member member);
}
