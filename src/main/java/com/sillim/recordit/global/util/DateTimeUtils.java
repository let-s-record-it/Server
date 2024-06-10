package com.sillim.recordit.global.util;

import com.sillim.recordit.global.exception.common.DayOfMonthOutOfRangeException;
import java.time.LocalDate;
import java.time.YearMonth;

public final class DateTimeUtils {

	/**
	 * LocalDate를 목표 dayOfMonth에 맞게 보정하여 반환한다.
	 * ex) yearMonth = YearMonth.of(2024, 2), dayofMonth = 31라면, LocalDate.of(2024, 2, 29)을 반환한다.
	 */
	public static LocalDate correctDayOfMonth(LocalDate date, int dayOfMonth) {
		if (dayOfMonth < 1 || dayOfMonth > 31) {
			throw new DayOfMonthOutOfRangeException();
		}
		YearMonth yearMonth = YearMonth.from(date);
		int lastDayOfMonth = yearMonth.lengthOfMonth();
		return yearMonth.atDay(Math.min(lastDayOfMonth, dayOfMonth));
	}
}
