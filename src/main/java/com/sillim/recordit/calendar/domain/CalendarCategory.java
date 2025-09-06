package com.sillim.recordit.calendar.domain;

import com.sillim.recordit.calendar.domain.vo.CalendarCategoryName;
import com.sillim.recordit.calendar.domain.vo.CalendarColorHex;
import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.*;
import java.util.Objects;
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

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	public CalendarCategory(String colorHex, String name, boolean isDefault, Long memberId) {
		this.colorHex = new CalendarColorHex(colorHex);
		this.name = new CalendarCategoryName(name);
		this.isDefault = isDefault;
		this.deleted = false;
		this.memberId = memberId;
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
		return Objects.equals(this.memberId, memberId);
	}
}
