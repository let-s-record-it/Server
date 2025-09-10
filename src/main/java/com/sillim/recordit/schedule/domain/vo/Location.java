package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidLocationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

	private static final int MIN_LATITUDE = 0;
	private static final int MIN_LONGITUDE = 0;
	private static final int MAX_LATITUDE = 90;
	private static final int MAX_LONGITUDE = 180;

	@Column private double latitude;

	@Column private double longitude;

	public Location(double latitude, double longitude) {
		validate(latitude, longitude);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	private static void validate(double latitude, double longitude) {
		if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
			throw new InvalidLocationException(ErrorCode.LATITUDE_OUT_OF_RANGE);
		}

		if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
			throw new InvalidLocationException(ErrorCode.LONGITUDE_OUT_OF_RANGE);
		}
	}
}
