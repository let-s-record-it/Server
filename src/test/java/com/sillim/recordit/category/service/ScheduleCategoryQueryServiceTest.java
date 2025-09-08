package com.sillim.recordit.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleCategoryQueryServiceTest {

	@Mock
	ScheduleCategoryRepository scheduleCategoryRepository;
	@Mock
	CalendarMemberService calendarMemberService;
	@InjectMocks
	ScheduleCategoryQueryService scheduleCategoryQueryService;

	@Test
	@DisplayName("특정 멤버의 캘린더 카테고리들을 조회할 수 있다.")
	void searchCalendarCategoriesByMemberId() {
		long calendarId = 1L;
		long memberId = 2L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory calendarCategory = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId);
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		given(scheduleCategoryRepository.findByDeletedIsFalseAndCalendarId(eq(calendarId)))
				.willReturn(List.of(category));

		List<ScheduleCategory> categories = scheduleCategoryQueryService.searchScheduleCategories(calendarId, memberId);

		assertThat(categories.get(0)).isEqualTo(category);
	}

	@Test
	@DisplayName("특정 캘린더 카테고리를 조회할 수 있다.")
	void searchCalendarCategory() {
		long categoryId = 1L;
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory calendarCategory = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId);
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		given(scheduleCategoryRepository.findById(eq(categoryId))).willReturn(Optional.of(category));

		ScheduleCategory foundCategory = scheduleCategoryQueryService.searchScheduleCategory(categoryId);

		assertThat(foundCategory).isEqualTo(category);
	}
}
