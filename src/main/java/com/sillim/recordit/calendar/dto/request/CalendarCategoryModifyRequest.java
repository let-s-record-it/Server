package com.sillim.recordit.calendar.dto.request;

import com.sillim.recordit.global.validation.common.ColorHexValid;
import org.hibernate.validator.constraints.Length;

public record CalendarCategoryModifyRequest(
		@ColorHexValid String colorHex, @Length(min = 1, max = 10) String name) {}
