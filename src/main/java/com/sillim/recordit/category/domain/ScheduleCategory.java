package com.sillim.recordit.category.domain;

import com.sillim.recordit.category.domain.vo.ScheduleCategoryName;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.schedule.domain.vo.ScheduleColorHex;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_category_id", nullable = false)
	private Long id;

	@Embedded private ScheduleColorHex colorHex;

	@Embedded private ScheduleCategoryName name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public ScheduleCategory(String colorHex, String name, Member member) {
		this.colorHex = new ScheduleColorHex(colorHex);
		this.name = new ScheduleCategoryName(name);
		this.member = member;
	}

	public String getColorHex() {
		return colorHex.getColorHex();
	}

	public String getName() {
		return name.getName();
	}

	public void modify(String colorHex, String name) {
		this.colorHex = new ScheduleColorHex(colorHex);
		this.name = new ScheduleCategoryName(name);
	}

	public boolean isOwner(Long memberId) {
		return member.getId().equals(memberId);
	}
}
