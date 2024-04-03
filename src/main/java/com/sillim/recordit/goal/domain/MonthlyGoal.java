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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor
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
	private Integer year;

	@Column(nullable = false)
	private Integer month;

	@Column(nullable = false)
	private String colorHex;

	@Column(nullable = false)
	@ColumnDefault("false")
	private Boolean achieved;

	@Column(nullable = false)
	@ColumnDefault("false")
	private Boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public MonthlyGoal(
			String title,
			String description,
			Integer year,
			Integer month,
			String colorHex,
			Member member) {
		this.title = title;
		this.description = description;
		this.year = year;
		this.month = month;
		this.colorHex = colorHex;
		this.member = member;
	}
}
