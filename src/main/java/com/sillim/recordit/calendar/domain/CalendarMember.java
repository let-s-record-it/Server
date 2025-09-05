package com.sillim.recordit.calendar.domain;

import com.sillim.recordit.global.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
		name = "calendar_member",
		uniqueConstraints = {
			@UniqueConstraint(
					name = "CalendarAndMember",
					columnNames = {"member_id", "calendar_id"})
		})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarMember extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "calendar_member_id", nullable = false)
	private Long id;

	@Column(nullable = false)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	@Column(name = "member_id")
	private Long memberId;

	public CalendarMember(Calendar calendar, Long memberId) {
		this.deleted = false;
		this.calendar = calendar;
		this.memberId = memberId;
	}

	public void delete() {
		this.deleted = true;
	}
}
