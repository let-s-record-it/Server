package com.sillim.recordit.category.service;

import com.sillim.recordit.calendar.domain.Calendar;
import com.sillim.recordit.calendar.service.CalendarMemberService;
import com.sillim.recordit.calendar.service.CalendarQueryService;
import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.dto.request.ScheduleCategoryAddRequest;
import com.sillim.recordit.category.dto.request.ScheduleCategoryModifyRequest;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.enums.color.InitialColor;
import com.sillim.recordit.schedule.service.ScheduleCommandService;
import com.sillim.recordit.task.service.TaskCommandService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleCategoryCommandService {

	private final ScheduleCategoryRepository scheduleCategoryRepository;
	private final CalendarQueryService calendarQueryService;
	private final CalendarMemberService calendarMemberService;
	private final ScheduleCommandService scheduleCommandService;
	private final ScheduleCategoryQueryService scheduleCategoryQueryService;
	private final TaskCommandService taskCommandService;

	public List<Long> addInitialCategories(Long calendarId, Long memberId) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		return Arrays.stream(InitialColor.values())
				.map(color -> scheduleCategoryRepository
						.save(new ScheduleCategory(color.getColorHex(), color.getName(), color.isDefault(), calendar))
						.getId())
				.toList();
	}

	public Long addCategory(ScheduleCategoryAddRequest request, Long calendarId, Long memberId) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);
		Calendar calendar = calendarQueryService.searchByCalendarId(calendarId);
		return scheduleCategoryRepository
				.save(new ScheduleCategory(request.colorHex(), request.name(), false, calendar)).getId();
	}

	public void modifyCategory(ScheduleCategoryModifyRequest request, Long categoryId, Long calendarId, Long memberId) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);
		ScheduleCategory scheduleCategory = scheduleCategoryQueryService.searchScheduleCategory(categoryId);
		scheduleCategory.modify(request.colorHex(), request.name());
	}

	public void deleteCategory(Long categoryId, Long calendarId, Long memberId) {
		calendarMemberService.validateCalendarMember(calendarId, memberId);
		scheduleCategoryQueryService.searchScheduleCategory(categoryId).delete();
		scheduleCommandService.replaceScheduleCategoriesWithDefaultCategory(categoryId, calendarId, memberId);
		taskCommandService.replaceTaskCategoriesWithDefaultCategory(categoryId, calendarId, memberId);
	}
}
