package com.sillim.recordit.category.service;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.dto.request.ScheduleCategoryAddRequest;
import com.sillim.recordit.category.dto.request.ScheduleCategoryModifyRequest;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.enums.color.InitialColor;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
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
	private final MemberQueryService memberQueryService;
	private final ScheduleCommandService scheduleCommandService;
	private final ScheduleCategoryQueryService scheduleCategoryQueryService;
	private final TaskCommandService taskCommandService;

	public List<Long> addInitialCategories(Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		return Arrays.stream(InitialColor.values())
				.map(
						color ->
								scheduleCategoryRepository
										.save(
												new ScheduleCategory(
														color.getColorHex(),
														color.getName(),
														color.isDefault(),
														member))
										.getId())
				.toList();
	}

	public Long addCategory(ScheduleCategoryAddRequest request, Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		return scheduleCategoryRepository
				.save(new ScheduleCategory(request.colorHex(), request.name(), false, member))
				.getId();
	}

	public void modifyCategory(
			ScheduleCategoryModifyRequest request, Long categoryId, Long memberId) {
		ScheduleCategory scheduleCategory =
				scheduleCategoryQueryService.searchScheduleCategory(categoryId);
		if (!scheduleCategory.isOwner(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_SCHEDULE_CATEGORY_GET_REQUEST);
		}
		scheduleCategory.modify(request.colorHex(), request.name());
	}

	public void deleteCategory(Long categoryId, Long memberId) {
		ScheduleCategory scheduleCategory =
				scheduleCategoryQueryService.searchScheduleCategory(categoryId);
		if (!scheduleCategory.isOwner(memberId) || scheduleCategory.isDefault()) {
			throw new InvalidRequestException(ErrorCode.INVALID_SCHEDULE_CATEGORY_GET_REQUEST);
		}
		scheduleCommandService.replaceScheduleCategoriesWithDefaultCategory(categoryId, memberId);
		taskCommandService.replaceTaskCategoriesWithDefaultCategory(categoryId, memberId);
		scheduleCategory.delete();
	}
}
