package com.sillim.recordit.category.repository;

import com.sillim.recordit.category.domain.ScheduleCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, Long> {
	List<ScheduleCategory> findByMemberId(Long memberId);
}
