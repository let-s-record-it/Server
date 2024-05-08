package com.sillim.recordit.schedule.repository;

import com.sillim.recordit.schedule.domain.RepetitionPattern;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepetitionPatternRepository extends JpaRepository<RepetitionPattern, Long> {

	Optional<RepetitionPattern> findByScheduleGroupId(Long scheduleGroupId);
}
