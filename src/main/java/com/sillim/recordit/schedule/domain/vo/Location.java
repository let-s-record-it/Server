package com.sillim.recordit.schedule.domain.vo;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.schedule.InvalidLocationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

	private static final int LATITUDE_MIN = 0;
	private static final int LONGITUDE_MIN = 0;
	private static final int LATITUDE_MAX = 90;
	private static final int LONGITUDE_MAX = 180;

	@Column(nullable = false)
	private Boolean setLocation;

	@Column private BigDecimal latitude;

	@Column private BigDecimal longitude;

	private Location(Boolean setLocation, BigDecimal latitude, BigDecimal longitude) {
		this.setLocation = setLocation;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static Location noLocation() {
		return new Location(false, null, null);
	}

	public static Location create(double latitude, double longitude) {
		validate(latitude, longitude);
		return new Location(true, BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
	}

	private static void validate(double latitude, double longitude) {
		if (latitude < LATITUDE_MIN || latitude > LATITUDE_MAX) {
			throw new InvalidLocationException(ErrorCode.INVALID_LATITUDE);
		}

		if (longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX) {
			throw new InvalidLocationException(ErrorCode.INVALID_LONGITUDE);
		}
	}
}
