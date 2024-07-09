package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepetitionPatternRepository
		extends JpaRepository<TaskRepetitionPattern, Long> {

	@Query("select r from TaskRepetitionPattern r where r.taskGroup.id = :taskGroupId")
	Optional<TaskRepetitionPattern> findByTaskGroupId(Long taskGroupId);

	@Query(
			"update TaskRepetitionPattern tr set tr.deleted = true where tr.taskGroup.id ="
					+ " :taskGroupId")
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	void deleteByTaskGroupId(Long taskGroupId);
}
