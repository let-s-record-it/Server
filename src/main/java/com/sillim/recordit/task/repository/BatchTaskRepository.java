package com.sillim.recordit.task.repository;

import com.sillim.recordit.task.domain.Task;
import java.util.List;

public interface BatchTaskRepository {

	void saveAllBatch(List<Task> tasks);
}
