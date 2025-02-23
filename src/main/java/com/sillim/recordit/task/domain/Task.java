package com.sillim.recordit.task.domain;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.task.domain.vo.TaskDescription;
import com.sillim.recordit.task.domain.vo.TaskTitle;
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
public class Task extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id", nullable = false)
	private Long id;

	@Embedded private TaskTitle title;

	@Embedded private TaskDescription description;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean achieved = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_category_id")
	private ScheduleCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id", nullable = false)
	private Calendar calendar;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_group_id", nullable = false)
	private TaskGroup taskGroup;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean deleted = false;

	public Task(
			TaskTitle title,
			TaskDescription description,
			LocalDate date,
			ScheduleCategory category,
			Calendar calendar,
			TaskGroup taskGroup) {
		this.title = title;
		this.description = description;
		this.date = date;
		this.category = category;
		this.calendar = calendar;
		this.taskGroup = taskGroup;
	}

	@Builder
	public Task(
			String title,
			String description,
			LocalDate date,
			ScheduleCategory category,
			Calendar calendar,
			TaskGroup taskGroup) {
		this(
				new TaskTitle(title),
				new TaskDescription(description),
				date,
				category,
				calendar,
				taskGroup);
	}

	public void modify(
			String title,
			String description,
			LocalDate date,
			ScheduleCategory category,
			Calendar calendar,
			TaskGroup taskGroup) {
		this.title = new TaskTitle(title);
		this.description = new TaskDescription(description);
		this.date = date;
		this.category = category;
		this.calendar = calendar;
		this.taskGroup = taskGroup;
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

	public String getColorHex() {
		return category.getColorHex();
	}

	public boolean isRepeated() {
		return taskGroup.getIsRepeated();
	}
}
