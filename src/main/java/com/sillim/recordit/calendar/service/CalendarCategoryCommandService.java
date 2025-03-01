package com.sillim.recordit.calendar.service;

import com.sillim.recordit.calendar.domain.CalendarCategory;
import com.sillim.recordit.calendar.dto.request.CalendarCategoryAddRequest;
import com.sillim.recordit.calendar.dto.request.CalendarCategoryModifyRequest;
import com.sillim.recordit.calendar.repository.CalendarCategoryRepository;
import com.sillim.recordit.enums.color.InitialColor;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CalendarCategoryCommandService {

	private final CalendarCategoryRepository calendarCategoryRepository;
	private final CalendarCategoryQueryService calendarCategoryQueryService;
	private final MemberQueryService memberQueryService;
	private final CalendarCommandService calendarCommandService;

	public List<Long> addDefaultCategories(Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		return Arrays.stream(InitialColor.values())
				.map(
						color ->
								calendarCategoryRepository
										.save(
												new CalendarCategory(
														color.getColorHex(),
														color.getName(),
														color.isDefault(),
														member))
										.getId())
				.toList();
	}

	public Long addCategory(CalendarCategoryAddRequest request, Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		return calendarCategoryRepository
				.save(new CalendarCategory(request.colorHex(), request.name(), false, member))
				.getId();
	}

	public void modifyCategory(
			CalendarCategoryModifyRequest request, Long categoryId, Long memberId) {
		CalendarCategory calendarCategory =
				calendarCategoryQueryService.searchCalendarCategory(categoryId);
		if (!calendarCategory.isOwner(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_CALENDAR_CATEGORY_GET_REQUEST);
		}
		calendarCategory.modify(request.colorHex(), request.name());
	}

	public void removeCategory(Long categoryId, Long memberId) {
		CalendarCategory calendarCategory =
				calendarCategoryQueryService.searchCalendarCategory(categoryId);
		if (!calendarCategory.isOwner(memberId) || calendarCategory.isDefault()) {
			throw new InvalidRequestException(ErrorCode.INVALID_CALENDAR_CATEGORY_GET_REQUEST);
		}
		calendarCategory.delete();
		calendarCommandService.replaceCalendarCategoriesWithDefault(categoryId, memberId);
	}
}
