package com.sillim.recordit.goal.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.goal.domain.vo.GoalDescription;
import com.sillim.recordit.goal.domain.vo.GoalTitle;
import com.sillim.recordit.goal.domain.vo.MonthlyGoalPeriod;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class MonthlyGoal extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "monthly_goal_id")
	private Long id;

	@Embedded
	private GoalTitle title;

	@Embedded
	private GoalDescription description;

	@Embedded
	private MonthlyGoalPeriod period;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "monthly_goal_category_id")
	private ScheduleCategory category;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean achieved;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	@Builder
	public MonthlyGoal(final String title, final String description, final LocalDate startDate, final LocalDate endDate,
			final ScheduleCategory category, final Calendar calendar) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new MonthlyGoalPeriod(startDate, endDate);
		this.category = category;
		this.achieved = false;
		this.deleted = false;
		this.calendar = calendar;
	}

	public void modify(final String newTitle, final String newDescription, final LocalDate newStartDate,
			final LocalDate newEndDate, final ScheduleCategory category, final Calendar calendar) {
		this.title = new GoalTitle(newTitle);
		this.description = new GoalDescription(newDescription);
		this.period = new MonthlyGoalPeriod(newStartDate, newEndDate);
		this.category = category;
		this.calendar = calendar;
	}

	public void changeAchieveStatus(final Boolean status) {
		this.achieved = status;
	}

	public void remove() {
		this.deleted = true;
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
