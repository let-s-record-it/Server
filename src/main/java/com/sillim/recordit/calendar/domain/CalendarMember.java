package com.sillim.recordit.calendar.domain;

import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.member.domain.Member;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	public CalendarMember(Member member, Calendar calendar) {
		this.member = member;
		setCalendar(calendar);
	}

	private void setCalendar(Calendar calendar) {
		if (this.calendar != null) {
			this.calendar.getCalendarMembers().remove(this);
		}

		this.calendar = calendar;
		if (calendar != null) {
			calendar.getCalendarMembers().add(this);
		}
	}
}
