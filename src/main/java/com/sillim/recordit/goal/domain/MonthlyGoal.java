package com.sillim.recordit.goal.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.global.domain.BaseEntity;
import com.sillim.recordit.goal.domain.vo.GoalDescription;
import com.sillim.recordit.goal.domain.vo.GoalTitle;
import com.sillim.recordit.goal.domain.vo.MonthlyGoalPeriod;
import jakarta.persistence.*;
import java.time.LocalDate;
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
public class MonthlyGoal extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "monthly_goal_id")
	private Long id;

	@Embedded private GoalTitle title;

	@Embedded private GoalDescription description;

	@Embedded private MonthlyGoalPeriod period;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean achieved;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "monthly_goal_category_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private ScheduleCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "calendar_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Calendar calendar;

	@Builder
	public MonthlyGoal(
			final String title,
			final String description,
			final LocalDate startDate,
			final LocalDate endDate,
			final ScheduleCategory category,
			final Calendar calendar) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new MonthlyGoalPeriod(startDate, endDate);
		this.category = category;
		this.achieved = false;
		this.calendar = calendar;
	}

	public void modify(
			final String newTitle,
			final String newDescription,
			final LocalDate newStartDate,
			final LocalDate newEndDate,
			final ScheduleCategory category,
			final Calendar calendar) {
		this.title = new GoalTitle(newTitle);
		this.description = new GoalDescription(newDescription);
		this.period = new MonthlyGoalPeriod(newStartDate, newEndDate);
		this.category = category;
		this.calendar = calendar;
	}

	public void changeAchieveStatus(final Boolean status) {
		this.achieved = status;
	}

	public String getTitle() {
		return title.getTitle();
	}

	public String getDescription() {
		return description.getDescription();
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
}
