package com.sillim.recordit.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.repository.CalendarCategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarCategoryQueryServiceTest {
	@Mock
	CalendarCategoryRepository calendarCategoryRepository;
	@InjectMocks
	CalendarCategoryQueryService calendarCategoryQueryService;

	@Test
	@DisplayName("특정 멤버의 캘린더 카테고리들을 조회할 수 있다.")
	void searchCalendarCategoriesByMemberId() {
		long memberId = 1L;
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		given(calendarCategoryRepository.findByDeletedIsFalseAndMemberId(eq(memberId))).willReturn(List.of(category));

		List<CalendarCategory> categories = calendarCategoryQueryService.searchCalendarCategories(memberId);

		assertThat(categories.get(0)).isEqualTo(category);
	}

	@Test
	@DisplayName("특정 캘린더 카테고리를 조회할 수 있다.")
	void searchCalendarCategory() {
		long categoryId = 1L;
		long memberId = 1L;
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		given(calendarCategoryRepository.findById(eq(categoryId))).willReturn(Optional.of(category));

		CalendarCategory foundCategory = calendarCategoryQueryService.searchCalendarCategory(categoryId);

		assertThat(foundCategory).isEqualTo(category);
	}
}
