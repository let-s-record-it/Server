package com.sillim.recordit.calendar.domain;

import com.sillim.recordit.calendar.domain.vo.CalendarCategoryName;
import com.sillim.recordit.calendar.domain.vo.CalendarColorHex;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarCategory extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "calendar_category_id", nullable = false)
	private Long id;

	@Embedded private CalendarColorHex colorHex;

	@Embedded private CalendarCategoryName name;

	@Column(nullable = false)
	private boolean isDefault;

	@Column(nullable = false)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public CalendarCategory(String colorHex, String name, boolean isDefault, Member member) {
		this.colorHex = new CalendarColorHex(colorHex);
		this.name = new CalendarCategoryName(name);
		this.isDefault = isDefault;
		this.deleted = false;
		this.member = member;
	}

	public String getColorHex() {
		return colorHex.getColorHex();
	}

	public String getName() {
		return name.getName();
	}

	public void modify(String colorHex, String name) {
		this.colorHex = new CalendarColorHex(colorHex);
		this.name = new CalendarCategoryName(name);
	}

	public void delete() {
		this.deleted = true;
	}

	public boolean isOwner(Long memberId) {
		return member.getId().equals(memberId);
	}
}
