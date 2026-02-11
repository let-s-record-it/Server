package com.sillim.recordit.goal.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.global.domain.BaseEntity;
import com.sillim.recordit.goal.domain.vo.GoalDescription;
import com.sillim.recordit.goal.domain.vo.GoalTitle;
import com.sillim.recordit.goal.domain.vo.WeeklyGoalPeriod;
import jakarta.persistence.*;
import java.time.LocalDate;
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
public class WeeklyGoal extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "weekly_goal_id")
	private Long id;

	@Embedded private GoalTitle title;

	@Embedded private GoalDescription description;

	@Embedded private WeeklyGoalPeriod period;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean achieved;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "weekly_goal_category_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ScheduleCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "monthly_goal_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private MonthlyGoal relatedMonthlyGoal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "calendar_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Calendar calendar;

	@Builder
	public WeeklyGoal(
			final String title,
			final String description,
			final Integer week,
			final LocalDate startDate,
			final LocalDate endDate,
			final ScheduleCategory category,
			final MonthlyGoal relatedMonthlyGoal,
			final Calendar calendar) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new WeeklyGoalPeriod(week, startDate, endDate);
		this.category = category;
		this.achieved = false;
		this.relatedMonthlyGoal = relatedMonthlyGoal;
		this.calendar = calendar;
	}

	public void changeAchieveStatus(final Boolean status) {
		this.achieved = status;
	}

	public void modify(
			final String title,
			final String description,
			final Integer week,
			final LocalDate startDate,
			final LocalDate endDate,
			final ScheduleCategory category,
			final MonthlyGoal relatedMonthlyGoal,
			final Calendar calendar) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new WeeklyGoalPeriod(week, startDate, endDate);
		this.category = category;
		this.relatedMonthlyGoal = relatedMonthlyGoal;
		this.calendar = calendar;
	}

	public void modify(
			final String title,
			final String description,
			final Integer week,
			final LocalDate startDate,
			final LocalDate endDate,
			final ScheduleCategory category,
			final Calendar calendar) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new WeeklyGoalPeriod(week, startDate, endDate);
		this.category = category;
		this.calendar = calendar;
	}

	public void linkRelatedMonthlyGoal(final MonthlyGoal relatedMonthlyGoal) {
		this.relatedMonthlyGoal = relatedMonthlyGoal;
	}

	public void unlinkRelatedMonthlyGoal() {
		this.relatedMonthlyGoal = null;
	}

	public String getTitle() {
		return title.getTitle();
	}

	public String getDescription() {
		return description.getDescription();
	}

	public Integer getWeek() {
		return period.getWeek();
	}

	public LocalDate getStartDate() {
		return period.getStartDate();
	}

	public LocalDate getEndDate() {
		return period.getEndDate();
	}

	public String getColorHex() {
		return category.getColorHex();
	}

	public Optional<MonthlyGoal> getRelatedMonthlyGoal() {
		return Optional.ofNullable(relatedMonthlyGoal);
	}
}
