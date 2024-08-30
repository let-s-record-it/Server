package com.sillim.recordit.goal.domain;

import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.goal.domain.vo.GoalColorHex;
import com.sillim.recordit.goal.domain.vo.GoalDescription;
import com.sillim.recordit.goal.domain.vo.GoalTitle;
import com.sillim.recordit.goal.domain.vo.WeeklyGoalPeriod;
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
public class WeeklyGoal extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "weekly_goal_id")
	private Long id;

	@Embedded private GoalTitle title;

	@Embedded private GoalDescription description;

	@Embedded private WeeklyGoalPeriod period;

	@Embedded private GoalColorHex colorHex;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean achieved;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "monthly_goal_id")
	private MonthlyGoal relatedMonthlyGoal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean deleted;

	@Builder
	public WeeklyGoal(
			final String title,
			final String description,
			final Integer week,
			final LocalDate startDate,
			final LocalDate endDate,
			final String colorHex,
			final MonthlyGoal relatedMonthlyGoal,
			final Member member) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new WeeklyGoalPeriod(week, startDate, endDate);
		this.colorHex = new GoalColorHex(colorHex);
		this.achieved = false;
		this.relatedMonthlyGoal = relatedMonthlyGoal;
		this.member = member;
		this.deleted = false;
	}

	public void changeAchieveStatus(final Boolean status) {
		this.achieved = status;
	}

	public void validateAuthenticatedMember(Long memberId) {
		if (!isOwnedBy(memberId)) {
			throw new InvalidRequestException(ErrorCode.WEEKLY_GOAL_ACCESS_DENIED);
		}
	}

	public void modify(
			final String title,
			final String description,
			final Integer week,
			final LocalDate startDate,
			final LocalDate endDate,
			final String colorHex,
			final MonthlyGoal relatedMonthlyGoal) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new WeeklyGoalPeriod(week, startDate, endDate);
		this.colorHex = new GoalColorHex(colorHex);
		this.relatedMonthlyGoal = relatedMonthlyGoal;
	}

	public void modify(
			final String title,
			final String description,
			final Integer week,
			final LocalDate startDate,
			final LocalDate endDate,
			final String colorHex) {
		this.title = new GoalTitle(title);
		this.description = new GoalDescription(description);
		this.period = new WeeklyGoalPeriod(week, startDate, endDate);
		this.colorHex = new GoalColorHex(colorHex);
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
		return colorHex.getColorHex();
	}

	public Optional<MonthlyGoal> getRelatedMonthlyGoal() {
		return Optional.ofNullable(relatedMonthlyGoal);
	}

	private boolean isOwnedBy(Long memberId) {
		return this.member.equalsId(memberId);
	}
}
