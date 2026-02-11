package com.sillim.recordit.calendar.domain;

import com.sillim.recordit.global.domain.BaseEntity;
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
public class CalendarMember extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "calendar_member_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "calendar_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Calendar calendar;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	public CalendarMember(Calendar calendar, Long memberId) {
		this.calendar = calendar;
		this.memberId = memberId;
	}
}
