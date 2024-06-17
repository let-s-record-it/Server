package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.TaskGroup;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepetitionPatternRepository
		extends JpaRepository<TaskRepetitionPattern, Long> {

	void deleteByTaskGroup(TaskGroup taskGroup);
}
