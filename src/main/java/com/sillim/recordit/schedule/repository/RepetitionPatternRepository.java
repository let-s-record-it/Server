package com.sillim.recordit.schedule.repository;

import com.sillim.recordit.schedule.domain.RepetitionPattern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepetitionPatternRepository extends JpaRepository<RepetitionPattern, Long> {

}
