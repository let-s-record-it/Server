package com.sillim.recordit.category.dto.response;

import com.sillim.recordit.category.domain.ScheduleCategory;

public record ScheduleCategoryListResponse(Long id, String colorHex, String name, boolean isDefault) {

	public static ScheduleCategoryListResponse of(ScheduleCategory scheduleCategory) {
		return new ScheduleCategoryListResponse(scheduleCategory.getId(), scheduleCategory.getColorHex(),
				scheduleCategory.getName(), scheduleCategory.isDefault());
	}
}
