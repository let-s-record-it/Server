package com.sillim.recordit.category.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.fixture.CalendarCategoryFixture;
import com.sillim.recordit.calendar.fixture.CalendarFixture;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.dto.request.ScheduleCategoryAddRequest;
import com.sillim.recordit.category.dto.request.ScheduleCategoryModifyRequest;
import com.sillim.recordit.category.fixture.ScheduleCategoryFixture;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.fixture.MemberFixture;
import com.sillim.recordit.schedule.service.ScheduleCommandService;
import com.sillim.recordit.task.service.TaskCommandService;
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
	@Mock CalendarQueryService calendarQueryService;
	@Mock CalendarMemberService calendarMemberService;
	@Mock ScheduleCommandService scheduleCommandService;
	@Mock TaskCommandService taskCommandService;
	@Mock ScheduleCategoryQueryService scheduleCategoryQueryService;
	@InjectMocks ScheduleCategoryCommandService scheduleCategoryCommandService;

	@Test
	@DisplayName("기본 카테고리들을 추가할 수 있다.")
	void addInitialCategories() {
		long calendarId = 1L;
		long memberId = 2L;
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory calendarCategory =
				CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId);
		ScheduleCategory category = ScheduleCategoryFixture.DEFAULT.getScheduleCategory(calendar);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(scheduleCategoryRepository.save(any(ScheduleCategory.class))).willReturn(category);

		List<Long> categoryIds =
				scheduleCategoryCommandService.addInitialCategories(calendarId, memberId);

		assertThat(categoryIds).hasSize(8);
	}

	@Test
	@DisplayName("캘린더 카테고리를 추가할 수 있다.")
	void addCategory() {
		long calendarId = 1L;
		long memberId = 2L;
		long categoryId = 3L;
		ScheduleCategory category = mock(ScheduleCategory.class);
		Member member = MemberFixture.DEFAULT.getMember();
		CalendarCategory calendarCategory =
				CalendarCategoryFixture.DEFAULT.getCalendarCategory(memberId);
		Calendar calendar = CalendarFixture.DEFAULT.getCalendar(calendarCategory, memberId);
		ScheduleCategoryAddRequest request = new ScheduleCategoryAddRequest("aabbff", "name");
		given(category.getId()).willReturn(categoryId);
		given(calendarQueryService.searchByCalendarId(eq(calendarId))).willReturn(calendar);
		given(scheduleCategoryRepository.save(any(ScheduleCategory.class))).willReturn(category);

		Long id = scheduleCategoryCommandService.addCategory(request, calendarId, memberId);

		assertThat(id).isEqualTo(categoryId);
	}

	@Test
	@DisplayName("캘린더 소유자면 캘린더 카테고리를 수정할 수 있다.")
	void modifyCategoryIfCalendarOwner() {
		long calendarId = 1L;
		long memberId = 2L;
		long categoryId = 3L;
		ScheduleCategory category = mock(ScheduleCategory.class);
		ScheduleCategoryModifyRequest request = new ScheduleCategoryModifyRequest("aabbff", "name");
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);

		assertThatCode(
						() ->
								scheduleCategoryCommandService.modifyCategory(
										request, categoryId, calendarId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("캘린더 멤버가 아니면 캘린더 카테고리를 수정할 때 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionWhenModifyCategoryIfNotCalendarOwner() {
		long calendarId = 1L;
		long memberId = 2L;
		long categoryId = 3L;
		ScheduleCategoryModifyRequest request = new ScheduleCategoryModifyRequest("aabbff", "name");
		doThrow(new RecordNotFoundException(ErrorCode.CALENDAR_MEMBER_NOT_FOUND))
				.when(calendarMemberService)
				.validateCalendarMember(any(), any());

		assertThatCode(
						() ->
								scheduleCategoryCommandService.modifyCategory(
										request, categoryId, calendarId, memberId))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.CALENDAR_MEMBER_NOT_FOUND.getDescription());
	}

	@Test
	@DisplayName("캘린더 멤버면 캘린더 카테고리를 삭제할 수 있다.")
	void deleteCategoryIfCalendarOwner() {
		long calendarId = 1L;
		long memberId = 2L;
		long categoryId = 3L;
		ScheduleCategory category = mock(ScheduleCategory.class);
		given(scheduleCategoryQueryService.searchScheduleCategory(eq(categoryId)))
				.willReturn(category);

		assertThatCode(
						() ->
								scheduleCategoryCommandService.deleteCategory(
										categoryId, calendarId, memberId))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("캘린더 소유자가 아니면 캘린더 카테고리를 삭제할 때 InvalidRequestException이 발생한다.")
	void throwInvalidRequestExceptionWhenDeleteCategoryIfNotCalendarOwner() {
		long calendarId = 1L;
		long memberId = 2L;
		long categoryId = 3L;
		doThrow(new RecordNotFoundException(ErrorCode.CALENDAR_MEMBER_NOT_FOUND))
				.when(calendarMemberService)
				.validateCalendarMember(any(), any());

		assertThatCode(
						() ->
								scheduleCategoryCommandService.deleteCategory(
										categoryId, calendarId, memberId))
				.isInstanceOf(RecordNotFoundException.class)
				.hasMessage(ErrorCode.CALENDAR_MEMBER_NOT_FOUND.getDescription());
	}
}
