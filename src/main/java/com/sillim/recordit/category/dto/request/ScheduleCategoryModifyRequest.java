package com.sillim.recordit.category.dto.request;

import com.sillim.recordit.global.validation.common.ColorHexValid;
import org.hibernate.validator.constraints.Length;

public record ScheduleCategoryModifyRequest(@ColorHexValid String colorHex, @Length(min = 1, max = 10) String name) {
}
