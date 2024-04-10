package com.sillim.recordit.goal.domain;

import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SoftDelete;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SoftDelete
public class MonthlyGoal extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "monthly_goal_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private Integer goalYear;

	@Column(nullable = false)
	private Integer goalMonth;

	@Column(nullable = false)
	private String colorHex;

	@Column(nullable = false)
	@ColumnDefault("false")
	private Boolean achieved;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public MonthlyGoal(
			String title,
			String description,
			Integer goalYear,
			Integer goalMonth,
			String colorHex,
			Member member) {
		this.title = title;
		this.description = description;
		this.goalYear = goalYear;
		this.goalMonth = goalMonth;
		this.colorHex = colorHex;
		this.achieved = Boolean.FALSE;
		this.member = member;
	}

	public void modify(
			String title,
			String description,
			Integer goalYear,
			Integer goalMonth,
			String colorHex) {
		this.title = title;
		this.description = description;
		this.goalYear = goalYear;
		this.goalMonth = goalMonth;
		this.colorHex = colorHex;
	}
}
