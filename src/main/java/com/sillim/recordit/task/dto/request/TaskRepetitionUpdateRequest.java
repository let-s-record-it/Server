package com.sillim.recordit.task.dto.request;

import com.sillim.recordit.enums.date.WeekNumber;
import com.sillim.recordit.enums.date.Weekday;
import com.sillim.recordit.global.validation.task.ValidDayOfMonth;
import com.sillim.recordit.global.validation.task.ValidMonth;
import com.sillim.recordit.global.validation.task.ValidWeekdayBit;
import com.sillim.recordit.task.domain.TaskRepetitionType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Range;

public record TaskRepetitionUpdateRequest(
		@NotNull TaskRepetitionType repetitionType,
		@Range(min = 1, max = 999) Integer repetitionPeriod,
		@NotNull LocalDate repetitionStartDate,
		@NotNull LocalDate repetitionEndDate,
		@ValidMonth Integer monthOfYear,
		@ValidDayOfMonth Integer dayOfMonth,
		WeekNumber weekNumber,
		Weekday weekday,
		@ValidWeekdayBit Integer weekdayBit) {}
