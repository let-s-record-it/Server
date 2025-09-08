package com.sillim.recordit.gcp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import com.google.cloud.storage.Storage;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ImageUploadServiceTest {

	ImageUploadService imageUploadService;
	Storage mockStorage;

	@BeforeEach
	void initObjects() {
		mockStorage = Mockito.mock(Storage.class);
		this.imageUploadService = new ImageUploadService(mockStorage);
	}

	@Test
	@DisplayName("이미지를 업로드할 수 있다.")
	void uploadImagesFeedImages() throws IOException {
		ReflectionTestUtils.setField(imageUploadService, "bucketName", "1234");

		MockMultipartFile multipartFile = new MockMultipartFile("images", "image.jpg", "text/plain",
				"test1".getBytes(StandardCharsets.UTF_8));

		List<String> imageUrls = imageUploadService.uploadImages(List.of(multipartFile));

		assertThat(imageUrls).hasSize(1);
		assertThat(imageUrls.get(0)).startsWith("https://storage.googleapis.com/1234/");
	}

	@Test
	@DisplayName("이미지가 null이면 업로드를 수행하지 않는다.")
	void notUploadImagesIfImageIsNull() throws IOException {
		List<String> uploaded = imageUploadService.uploadImages(null);

		assertThat(uploaded).isEmpty();
	}

	@Test
	@DisplayName("이미지가 비어있으면 FileNotFoundException이 발생한다.")
	void throwFileNotFoundExceptionIfFeedImageFileNotExists() {
		MockMultipartFile multipartFile = new MockMultipartFile("images", "image.jpg", "text/plain",
				"".getBytes(StandardCharsets.UTF_8));

		assertThatCode(() -> imageUploadService.uploadImages(List.of(multipartFile)))
				.isInstanceOf(FileNotFoundException.class).hasMessage(ErrorCode.FILE_NOT_FOUND.getDescription());
	}
}
