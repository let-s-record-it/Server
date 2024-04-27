package com.sillim.recordit.schedule.dto;

import com.sillim.recordit.global.validation.schedule.ValidDayOfMonth;
import com.sillim.recordit.global.validation.schedule.ValidMonth;
import com.sillim.recordit.global.validation.schedule.ValidWeekdayBit;
import com.sillim.recordit.schedule.domain.RepetitionType;
import com.sillim.recordit.schedule.domain.WeekNumber;
import com.sillim.recordit.schedule.domain.Weekday;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.Range;

public record RepetitionAddRequest(@NotNull RepetitionType repetitionType,
                                   @Range(min = 1, max = 999) Integer repetitionPeriod,
                                   @NotNull LocalDateTime repetitionStartDate,
                                   @NotNull LocalDateTime repetitionEndDate,
                                   @ValidMonth Integer monthOfYear,
                                   @ValidDayOfMonth Integer dayOfMonth,
                                   WeekNumber weekNumber,
                                   Weekday weekday,
                                   @ValidWeekdayBit Integer weekdayBit) {

}
