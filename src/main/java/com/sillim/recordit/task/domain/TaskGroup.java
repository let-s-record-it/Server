package com.sillim.recordit.task.domain;

import com.sillim.recordit.global.domain.BaseEntity;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import com.sillim.recordit.task.domain.repetition.TaskRepetitionPattern;
import jakarta.persistence.*;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted = false")
public class TaskGroup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_group_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private Boolean isRepeated;

	@OneToOne(mappedBy = "taskGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private TaskRepetitionPattern repetitionPattern;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "monthly_goal_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private MonthlyGoal monthlyGoal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "weekly_goal_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private WeeklyGoal weeklyGoal;

	public TaskGroup(final MonthlyGoal monthlyGoal, final WeeklyGoal weeklyGoal) {
		this.isRepeated = false;
		this.monthlyGoal = monthlyGoal;
		this.weeklyGoal = weeklyGoal;
	}

	public void modify(final MonthlyGoal monthlyGoal, final WeeklyGoal weeklyGoal) {
		this.monthlyGoal = monthlyGoal;
		this.weeklyGoal = weeklyGoal;
	}

	public void setRepetitionPattern(final TaskRepetitionPattern repetitionPattern) {
		this.isRepeated = true;
		this.repetitionPattern = repetitionPattern;
		this.repetitionPattern.setTaskGroup(this);
	}

	public void removeRepetitionPattern() {
		this.isRepeated = false;
		this.repetitionPattern.remove();
		this.repetitionPattern = null;
	}

	public Optional<MonthlyGoal> getMonthlyGoal() {
		return Optional.ofNullable(monthlyGoal);
	}

	public Optional<WeeklyGoal> getWeeklyGoal() {
		return Optional.ofNullable(weeklyGoal);
	}

	public Optional<TaskRepetitionPattern> getRepetitionPattern() {
		return Optional.ofNullable(repetitionPattern);
	}
}
