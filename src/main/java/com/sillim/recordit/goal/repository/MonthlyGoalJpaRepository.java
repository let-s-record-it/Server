package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.MonthlyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyGoalJpaRepository extends JpaRepository<MonthlyGoal, Long> {}
