package com.sillim.recordit.calendar.domain;

import com.sillim.recordit.calendar.domain.vo.CalendarColorHex;
import com.sillim.recordit.calendar.domain.vo.CalendarTitle;
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
public class Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "calendar_id", nullable = false)
	private Long id;

	@Embedded private CalendarTitle title;

	@Embedded private CalendarColorHex colorHex;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public Calendar(String title, String colorHex, Member member) {
		this.title = new CalendarTitle(title);
		this.colorHex = new CalendarColorHex(colorHex);
		this.member = member;
	}

	public String getTitle() {
		return title.getTitle();
	}

	public String getColorHex() {
		return colorHex.getColorHex();
	}

	public boolean isOwnedBy(Long memberId) {
		return this.member.equalsId(memberId);
	}

	public void validateAuthenticatedMember(Long memberId) {
		if (!isOwnedBy(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
		}
	}
}
