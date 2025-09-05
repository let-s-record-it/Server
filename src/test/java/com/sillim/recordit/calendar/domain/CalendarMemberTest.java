package com.sillim.recordit.calendar.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CalendarMemberTest {

	@Test
	@DisplayName("캘린더 멤버를 삭제할 수 있다.")
	void saveRelatedEntityInCalendarWhenSaveCalendarMember() {
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(category, memberId);
		CalendarMember calendarMember = new CalendarMember(calendar, memberId);
		calendarMember.delete();

		assertThat(calendarMember.isDeleted()).isTrue();
	}
}
