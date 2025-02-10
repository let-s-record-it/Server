package com.sillim.recordit.category.service;

import com.sillim.recordit.category.domain.ScheduleCategory;
import com.sillim.recordit.category.dto.request.ScheduleCategoryAddRequest;
import com.sillim.recordit.category.dto.request.ScheduleCategoryModifyRequest;
import com.sillim.recordit.category.repository.ScheduleCategoryRepository;
import com.sillim.recordit.enums.color.DefaultColor;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.InvalidRequestException;
import com.sillim.recordit.global.exception.common.RecordNotFoundException;
import com.sillim.recordit.member.domain.Member;
import com.sillim.recordit.member.service.MemberQueryService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleCategoryService {

	private final ScheduleCategoryRepository scheduleCategoryRepository;
	private final MemberQueryService memberQueryService;

	public List<ScheduleCategory> searchScheduleCategories(Long memberId) {
		return scheduleCategoryRepository.findByMemberId(memberId);
	}

	public ScheduleCategory searchScheduleCategory(Long categoryId) {
		return scheduleCategoryRepository
				.findById(categoryId)
				.orElseThrow(
						() -> new RecordNotFoundException(ErrorCode.SCHEDULE_CATEGORY_NOT_FOUND));
	}

	@Transactional
	public List<Long> addDefaultCategories(Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		return Arrays.stream(DefaultColor.values())
				.map(
						color ->
								scheduleCategoryRepository
										.save(
												new ScheduleCategory(
														color.getColorHex(),
														color.getName(),
														member))
										.getId())
				.toList();
	}

	@Transactional
	public Long addCategory(ScheduleCategoryAddRequest request, Long memberId) {
		Member member = memberQueryService.findByMemberId(memberId);
		return scheduleCategoryRepository
				.save(new ScheduleCategory(request.colorHex(), request.name(), member))
				.getId();
	}

	@Transactional
	public void modifyCategory(
			ScheduleCategoryModifyRequest request, Long categoryId, Long memberId) {
		ScheduleCategory scheduleCategory = searchScheduleCategory(categoryId);
		if (!scheduleCategory.isOwner(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_SCHEDULE_CATEGORY_GET_REQUEST);
		}
		scheduleCategory.modify(request.colorHex(), request.name());
	}

	@Transactional
	public void deleteCategory(Long categoryId, Long memberId) {
		ScheduleCategory scheduleCategory = searchScheduleCategory(categoryId);
		if (!scheduleCategory.isOwner(memberId)) {
			throw new InvalidRequestException(ErrorCode.INVALID_SCHEDULE_CATEGORY_GET_REQUEST);
		}
		scheduleCategoryRepository.delete(scheduleCategory);
	}
}
