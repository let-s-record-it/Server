package com.sillim.recordit.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.FileGenerateException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileUtilsTest {

	@Test
	@DisplayName("파일 이름에 UUID를 붙일 수 있다.")
	void generateUUIDFileName() {
		String fileName = "file.jpg";
		String generatedFileName = FileUtils.generateUUIDFileName(fileName);

		assertThat(generatedFileName.split("_")).hasSize(2);
		assertThat(generatedFileName.split("_")[1]).isEqualTo(fileName);
	}

	@Test
	@DisplayName("파일에 이미지 저장을 위한 경로를 붙일 수 있다.")
	void addImagePath() {
		String fileName = "file.jpg";
		String imagePath = FileUtils.toImagePath(fileName);

		assertThat(imagePath).isEqualTo("images/file.jpg");
	}

	@Test
	@DisplayName("MultipartFile을 File로 변환할 수 있다.")
	void multipartFileConvertToFile() throws IOException {
		MockMultipartFile multipartFile = new MockMultipartFile("images", "image.jpg", "text/plain",
				"test1".getBytes(StandardCharsets.UTF_8));

		File file = FileUtils.convert(multipartFile, "image.jpg");

		assertThat(file.getName()).endsWith(multipartFile.getOriginalFilename());
		file.delete();
	}

	@Test
	@DisplayName("파일이 이미 존재하면 FileGenerateException이 발생한다.")
	void throwFileGenerateExceptionIfAlreadyExists() throws IOException {
		MockMultipartFile multipartFile = new MockMultipartFile("images", "image.jpg", "text/plain",
				"test1".getBytes(StandardCharsets.UTF_8));

		File file = FileUtils.convert(multipartFile, "image");
		assertThatCode(() -> FileUtils.convert(multipartFile, "image")).isInstanceOf(FileGenerateException.class)
				.hasMessage(ErrorCode.FILE_GENERATE_FAIL.getDescription());

		file.delete();
	}
}
