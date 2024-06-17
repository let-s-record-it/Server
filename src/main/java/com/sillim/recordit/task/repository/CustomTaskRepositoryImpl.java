package com.sillim.recordit.task.repository;

import static com.sillim.recordit.task.domain.QTask.task;

import com.querydsl.core.BooleanBuilder;
import com.sillim.recordit.global.querydsl.QuerydslRepositorySupport;
import com.sillim.recordit.task.domain.Task;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Repository;

@Repository
public class CustomTaskRepositoryImpl extends QuerydslRepositorySupport
		implements CustomTaskRepository {

	public CustomTaskRepositoryImpl() {
		super(Task.class);
	}

	@Override
	public void deleteAllByTaskGroupId(Long taskGroupId, LocalDate date) {

		delete(task).where(task.taskGroup.id.eq(taskGroupId).and(dateAfterOrEqual(date)));
		getEntityManager().flush();
		getEntityManager().clear();
	}

	@Override
	public void deleteAllByTaskGroupIdAndAchievedIsFalse(Long taskGroupId, LocalDate date) {

		delete(task)
				.where(
						task.taskGroup
								.id
								.eq(taskGroupId)
								.and(task.achieved.eq(false))
								.and(dateAfterOrEqual(date)));
		getEntityManager().flush();
		getEntityManager().clear();
	}

	@Override
	public boolean existsByTaskGroupIdAndDateEquals(Long taskGroupId, LocalDate date) {
		return selectFrom(task)
						.where(task.taskGroup.id.eq(taskGroupId).and(task.date.eq(date)))
						.fetchFirst()
				!= null;
	}

	private BooleanBuilder dateAfterOrEqual(LocalDate date) {

		BooleanBuilder builder = new BooleanBuilder();
		if (Objects.nonNull(date)) {
			builder.and(task.date.goe(date));
		}
		return builder;
	}
}
