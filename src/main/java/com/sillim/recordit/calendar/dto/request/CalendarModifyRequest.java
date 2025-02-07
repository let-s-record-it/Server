package com.sillim.recordit.calendar.dto.request;

import com.sillim.recordit.global.validation.common.ColorHexValid;
import org.hibernate.validator.constraints.Length;

public record CalendarModifyRequest(
		@Length(min = 1, max = 30) String title, @ColorHexValid String colorHex) {}
