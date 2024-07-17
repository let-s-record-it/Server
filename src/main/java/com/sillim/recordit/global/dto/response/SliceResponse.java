package com.sillim.recordit.global.dto.response;

import java.util.List;
import org.springframework.data.domain.Slice;

public record SliceResponse<T>(
		List<T> content,
		boolean isSorted,
		int currentPage,
		int size,
		boolean isFirst,
		boolean isLast) {

	public static <T> SliceResponse<T> of(Slice<T> sliceContent) {
		return new SliceResponse<>(
				sliceContent.getContent(),
				sliceContent.getSort().isSorted(),
				sliceContent.getNumber(),
				sliceContent.getSize(),
				sliceContent.isFirst(),
				sliceContent.isLast());
	}
}
