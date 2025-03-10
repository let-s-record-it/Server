package com.sillim.recordit.calendar.domain;

import com.sillim.recordit.calendar.domain.vo.CalendarTitle;
import com.sillim.recordit.global.domain.BaseTime;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "calendar_id", nullable = false)
	private Long id;

	@Embedded private CalendarTitle title;

	@Column(nullable = false)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_category_id")
	private CalendarCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public Calendar(String title, Member member, CalendarCategory category) {
		this.title = new CalendarTitle(title);
		this.deleted = false;
		this.member = member;
		this.category = category;
	}

	public String getTitle() {
		return title.getTitle();
	}

	public boolean isOwnedBy(Long memberId) {
		return this.member.equalsId(memberId);
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

	public void delete() {
		this.deleted = true;
	}

	public String getColorHex() {
		return category.getColorHex();
	}
}
