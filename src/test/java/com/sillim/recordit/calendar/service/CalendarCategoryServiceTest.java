package com.sillim.recordit.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.dto.request.CalendarCategoryAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarCategoryModifyRequest;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.repository.CalendarCategoryRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalendarCategoryServiceTest {

	@Mock CalendarCategoryRepository calendarCategoryRepository;
	@Mock MemberQueryService memberQueryService;
	@InjectMocks CalendarCategoryService calendarCategoryService;

	@Test
	@DisplayName("특정 멤버의 캘린더 카테고리들을 조회할 수 있다.")
	void searchCalendarCategoriesByMemberId() {
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		given(calendarCategoryRepository.findByMemberId(eq(memberId)))
				.willReturn(List.of(category));

		List<CalendarCategory> categories =
				calendarCategoryService.searchCalendarCategories(memberId);

		assertThat(categories.get(0)).isEqualTo(category);
	}

	@Test
	@DisplayName("특정 캘린더 카테고리를 조회할 수 있다.")
	void searchCalendarCategory() {
		long categoryId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		given(calendarCategoryRepository.findById(eq(categoryId)))
				.willReturn(Optional.of(category));

		CalendarCategory foundCategory = calendarCategoryService.searchCalendarCategory(categoryId);

		assertThat(foundCategory).isEqualTo(category);
	}

	@Test
	@DisplayName("기본 카테고리들을 추가할 수 있다.")
	void addDefaultCategories() {
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = CalendarCategoryFixture.DEFAULT.getCalendarCategory(member);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(calendarCategoryRepository.save(any(CalendarCategory.class))).willReturn(category);

		List<Long> categoryIds = calendarCategoryService.addDefaultCategories(memberId);

		assertThat(categoryIds).hasSize(8);
	}

	@Test
	@DisplayName("캘린더 카테고리를 추가할 수 있다.")
	void addCategory() {
		long memberId = 1L;
		long categoryId = 2L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory category = mock(CalendarCategory.class);
		CalendarCategoryAddRequest request = new CalendarCategoryAddRequest("aabbff", "name");
		given(category.getId()).willReturn(categoryId);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(calendarCategoryRepository.save(any(CalendarCategory.class))).willReturn(category);

		Long id = calendarCategoryService.addCategory(request, memberId);

		assertThat(id).isEqualTo(categoryId);
	}

	@Test
	@DisplayName("캘린더 소유자면 캘린더 카테고리를 수정할 수 있다.")
	void modifyCategoryIfCalendarOwner() {
		long memberId = 1L;
		long categoryId = 2L;
		CalendarCategory category = mock(CalendarCategory.class);
		CalendarCategoryModifyRequest request = new CalendarCategoryModifyRequest("aabbff", "name");
		given(category.isOwner(eq(memberId))).willReturn(true);
		given(calendarCategoryRepository.findById(eq(categoryId)))
				.willReturn(Optional.of(category));

		assertThatCode(() -> calendarCategoryService.modifyCategory(request, categoryId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("캘린더 소유자가 아니면 캘린더 카테고리를 수정할 때 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionWhenModifyCategoryIfNotCalendarOwner() {
		long memberId = 1L;
		long categoryId = 2L;
		CalendarCategory category = mock(CalendarCategory.class);
		CalendarCategoryModifyRequest request = new CalendarCategoryModifyRequest("aabbff", "name");
		given(category.isOwner(eq(memberId))).willReturn(false);
		given(calendarCategoryRepository.findById(eq(categoryId)))
				.willReturn(Optional.of(category));

		assertThatCode(() -> calendarCategoryService.modifyCategory(request, categoryId, memberId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_CALENDAR_CATEGORY_GET_REQUEST.getDescription());
	}

	@Test
	@DisplayName("캘린더 소유자면 캘린더 카테고리를 삭제할 수 있다.")
	void deleteCategoryIfCalendarOwner() {
		long memberId = 1L;
		long categoryId = 2L;
		CalendarCategory category = mock(CalendarCategory.class);
		given(category.isOwner(eq(memberId))).willReturn(true);
		given(calendarCategoryRepository.findById(eq(categoryId)))
				.willReturn(Optional.of(category));

		assertThatCode(() -> calendarCategoryService.deleteCategory(categoryId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("캘린더 소유자가 아니면 캘린더 카테고리를 삭제할 때 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionWhenDeleteCategoryIfNotCalendarOwner() {
		long memberId = 1L;
		long categoryId = 2L;
		CalendarCategory category = mock(CalendarCategory.class);
		given(category.isOwner(eq(memberId))).willReturn(false);
		given(calendarCategoryRepository.findById(eq(categoryId)))
				.willReturn(Optional.of(category));

		assertThatCode(() -> calendarCategoryService.deleteCategory(categoryId, memberId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_CALENDAR_CATEGORY_GET_REQUEST.getDescription());
	}
}
