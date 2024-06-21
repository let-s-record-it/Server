package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepetitionPatternRepository
		extends JpaRepository<TaskRepetitionPattern, Long> {

	@Query("select r from TaskRepetitionPattern r where r.taskGroup.id = :taskGroup")
	Optional<TaskRepetitionPattern> findByTaskGroupId(Long taskGroup);
}
