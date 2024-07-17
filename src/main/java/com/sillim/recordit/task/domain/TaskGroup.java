package com.sillim.recordit.task.domain;

import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.goal.domain.MonthlyGoal;
import com.sillim.recordit.goal.domain.WeeklyGoal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted = false")
public class TaskGroup extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_group_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private Boolean isRepeated;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "monthly_goal_id")
	private MonthlyGoal monthlyGoal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "weekly_goal_id")
	private WeeklyGoal weeklyGoal;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean deleted = false;

	@Builder
	public TaskGroup(
			final Boolean isRepeated, final MonthlyGoal monthlyGoal, final WeeklyGoal weeklyGoal) {
		this.isRepeated = isRepeated;
		this.monthlyGoal = monthlyGoal;
		this.weeklyGoal = weeklyGoal;
	}

	public void modify(
			final Boolean isRepeated, final MonthlyGoal monthlyGoal, final WeeklyGoal weeklyGoal) {
		this.isRepeated = isRepeated;
		this.monthlyGoal = monthlyGoal;
		this.weeklyGoal = weeklyGoal;
	}

	public Optional<MonthlyGoal> getMonthlyGoal() {
		return Optional.ofNullable(monthlyGoal);
	}

	public Optional<WeeklyGoal> getWeeklyGoal() {
		return Optional.ofNullable(weeklyGoal);
	}
}
