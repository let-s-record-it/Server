package com.sillim.recordit.goal.domain;

import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.goal.domain.vo.GoalColorHex;
import com.sillim.recordit.goal.domain.vo.GoalDescription;
import com.sillim.recordit.goal.domain.vo.GoalTitle;
import com.sillim.recordit.goal.domain.vo.MonthlyGoalPeriod;
import com.sillim.recordit.member.domain.Member;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE monthly_goal SET deleted = true WHERE monthly_goal_id = ?")
@SQLRestriction("deleted = false")
public class MonthlyGoal extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "monthly_goal_id")
	private Long id;

	@Embedded private GoalTitle title;

	@Embedded private GoalDescription description;

	@Embedded private MonthlyGoalPeriod period;

	@Embedded private GoalColorHex colorHex;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean achieved;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean deleted;

	@Builder
	public MonthlyGoal(
			final String title,
			final String description,
			final LocalDate startDate,
			final LocalDate endDate,
			final String colorHex,
			final Member member) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new MonthlyGoalPeriod(startDate, endDate);
		this.colorHex = new GoalColorHex(colorHex);
		this.achieved = false;
		this.member = member;
		this.deleted = false;
	}

	public void modify(
			final String newTitle,
			final String newDescription,
			final LocalDate newStartDate,
			final LocalDate newEndDate,
			final String newColorHex) {
		this.title = new GoalTitle(newTitle);
		this.description = new GoalDescription(newDescription);
		this.period = new MonthlyGoalPeriod(newStartDate, newEndDate);
		this.colorHex = new GoalColorHex(newColorHex);
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
		return colorHex.getColorHex();
	}
}
