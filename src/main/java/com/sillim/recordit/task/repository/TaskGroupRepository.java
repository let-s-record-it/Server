package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long> {
}
