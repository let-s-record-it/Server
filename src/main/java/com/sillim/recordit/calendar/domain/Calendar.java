package com.sillim.recordit.calendar.domain;

import com.sillim.recordit.calendar.domain.vo.CalendarTitle;
import com.sillim.recordit.global.domain.BaseEntity;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "calendar_id", nullable = false)
	private Long id;

	@Embedded private CalendarTitle title;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "calendar_category_id",
			nullable = false,
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private CalendarCategory category;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	public Calendar(String title, CalendarCategory category, Long memberId) {
		this.title = new CalendarTitle(title);
		this.memberId = memberId;
		this.category = category;
	}

	public String getTitle() {
		return title.getTitle();
	}

	public boolean isOwnedBy(Long memberId) {
		return Objects.equals(this.memberId, memberId);
	}

	public void validateAuthenticatedMember(Long memberId) {
		if (!isOwnedBy(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}
	}

	public void modify(String title, CalendarCategory category) {
		this.title = new CalendarTitle(title);
		this.category = category;
	}

	public String getColorHex() {
		return category.getColorHex();
	}
}
