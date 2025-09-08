package com.sillim.recordit.schedule.dto.response;

import com.sillim.recordit.schedule.domain.vo.WeekdayBit;
import java.time.DayOfWeek;
import lombok.Builder;

@Builder
public record RepeatedWeekday(Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday,
		Boolean saturday, Boolean sunday) {

	public static RepeatedWeekday from(WeekdayBit weekdayBit) {
		return RepeatedWeekday.builder().monday(weekdayBit.isValidWeekday(DayOfWeek.MONDAY))
				.tuesday(weekdayBit.isValidWeekday(DayOfWeek.TUESDAY))
				.wednesday(weekdayBit.isValidWeekday(DayOfWeek.WEDNESDAY))
				.thursday(weekdayBit.isValidWeekday(DayOfWeek.THURSDAY))
				.friday(weekdayBit.isValidWeekday(DayOfWeek.FRIDAY))
				.saturday(weekdayBit.isValidWeekday(DayOfWeek.SATURDAY))
				.sunday(weekdayBit.isValidWeekday(DayOfWeek.SUNDAY)).build();
	}
}
