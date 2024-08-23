package com.sillim.recordit.goal.repository;

import com.sillim.recordit.goal.domain.WeeklyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyGoalRepository extends JpaRepository<WeeklyGoal, Long> {}
