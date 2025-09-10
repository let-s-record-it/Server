package com.sillim.recordit.category.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.vo.ScheduleCategoryName;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.schedule.domain.vo.ScheduleColorHex;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCategory extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "schedule_category_id", nullable = false)
	private Long id;

	@Embedded private ScheduleColorHex colorHex;

	@Embedded private ScheduleCategoryName name;

	@Column(nullable = false)
	private boolean isDefault;

	@Column(nullable = false)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	public ScheduleCategory(String colorHex, String name, boolean isDefault, Calendar calendar) {
		this.colorHex = new ScheduleColorHex(colorHex);
		this.name = new ScheduleCategoryName(name);
		this.isDefault = isDefault;
		this.deleted = false;
		this.calendar = calendar;
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

	public void delete() {
		this.deleted = true;
	}
}
