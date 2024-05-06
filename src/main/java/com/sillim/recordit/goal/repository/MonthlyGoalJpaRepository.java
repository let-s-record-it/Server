package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyGoalJpaRepository extends JpaRepository<MonthlyGoal, Long> {

	Optional<MonthlyGoal> findByIdAndMemberId(Long monthlyGoalId, Long memberId);

	List<MonthlyGoal> findByPeriod_StartDateAndPeriod_EndDateAndMember_Id(
			LocalDate startDate, LocalDate endDate, Long memberId);
}
