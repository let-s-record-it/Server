package com.sillim.recordit.task.dto.request;

import com.sillim.recordit.global.validation.common.ColorHexValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

public record TaskUpdateRequest(
		@NotBlank @Length(max = 30) String title,
		@Length(max = 500) String description,
		@NotNull LocalDate date,
		@ColorHexValid String colorHex,
		@NotNull Long calendarId,
		@Validated TaskGroupUpdateRequest taskGroup) {}
