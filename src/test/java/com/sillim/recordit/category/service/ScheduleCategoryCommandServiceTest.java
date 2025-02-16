package com.sillim.recordit.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.dto.request.ScheduleCategoryAddRequest;
import com.sillim.recordit.category.dto.request.ScheduleCategoryModifyRequest;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.member.service.MemberQueryService;
import com.sillim.recordit.schedule.service.ScheduleCommandService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleCategoryCommandServiceTest {

	@Mock ScheduleCategoryRepository scheduleCategoryRepository;
	@Mock MemberQueryService memberQueryService;
	@Mock ScheduleCommandService scheduleCommandService;
	@Mock ScheduleCategoryQueryService scheduleCategoryQueryService;
	@InjectMocks ScheduleCategoryCommandService scheduleCategoryCommandService;

	@Test
	@DisplayName("기본 카테고리들을 추가할 수 있다.")
	void addInitialCategories() {
		long memberId = 1L;
		Member member = MemberFixture.DEFAULT.getMember();
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(member);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(scheduleCategoryRepository.save(any(ScheduleCategory.class))).willReturn(category);

		List<Long> categoryIds = scheduleCategoryCommandService.addInitialCategories(memberId);

		assertThat(categoryIds).hasSize(8);
	}

	@Test
	@DisplayName("캘린더 카테고리를 추가할 수 있다.")
	void addCategory() {
		long memberId = 1L;
		long categoryId = 2L;
		Member member = MemberFixture.DEFAULT.getMember();
		ScheduleCategory category = mock(ScheduleCategory.class);
		ScheduleCategoryAddRequest request = new ScheduleCategoryAddRequest("aabbff", "name");
		given(category.getId()).willReturn(categoryId);
		given(memberQueryService.findByMemberId(eq(memberId))).willReturn(member);
		given(scheduleCategoryRepository.save(any(ScheduleCategory.class))).willReturn(category);

		Long id = scheduleCategoryCommandService.addCategory(request, memberId);

		assertThat(id).isEqualTo(categoryId);
	}

	@Test
	@DisplayName("캘린더 소유자면 캘린더 카테고리를 수정할 수 있다.")
	void modifyCategoryIfCalendarOwner() {
		long memberId = 1L;
		long categoryId = 2L;
		ScheduleCategory category = mock(ScheduleCategory.class);
		ScheduleCategoryModifyRequest request = new ScheduleCategoryModifyRequest("aabbff", "name");
		given(category.isOwner(eq(memberId))).willReturn(true);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);

		assertThatCode(
						() ->
								scheduleCategoryCommandService.modifyCategory(
										request, categoryId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("캘린더 소유자가 아니면 캘린더 카테고리를 수정할 때 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionWhenModifyCategoryIfNotCalendarOwner() {
		long memberId = 1L;
		long categoryId = 2L;
		ScheduleCategory category = mock(ScheduleCategory.class);
		ScheduleCategoryModifyRequest request = new ScheduleCategoryModifyRequest("aabbff", "name");
		given(category.isOwner(eq(memberId))).willReturn(false);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);

		assertThatCode(
						() ->
								scheduleCategoryCommandService.modifyCategory(
										request, categoryId, memberId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_SCHEDULE_CATEGORY_GET_REQUEST.getDescription());
	}

	@Test
	@DisplayName("캘린더 소유자면 캘린더 카테고리를 삭제할 수 있다.")
	void deleteCategoryIfCalendarOwner() {
		long memberId = 1L;
		long categoryId = 2L;
		ScheduleCategory category = mock(ScheduleCategory.class);
		given(category.isOwner(eq(memberId))).willReturn(true);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);

		assertThatCode(() -> scheduleCategoryCommandService.deleteCategory(categoryId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("캘린더 소유자가 아니면 캘린더 카테고리를 삭제할 때 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionWhenDeleteCategoryIfNotCalendarOwner() {
		long memberId = 1L;
		long categoryId = 2L;
		ScheduleCategory category = mock(ScheduleCategory.class);
		given(category.isOwner(eq(memberId))).willReturn(false);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);

		assertThatCode(() -> scheduleCategoryCommandService.deleteCategory(categoryId, memberId))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.INVALID_SCHEDULE_CATEGORY_GET_REQUEST.getDescription());
	}
}
