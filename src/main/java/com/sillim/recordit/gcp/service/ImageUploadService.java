package com.sillim.recordit.gcp.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.FileNotFoundException;
import com.sillim.recordit.global.util.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImageUploadService {

	private final Storage storage;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	public List<String> uploadImages(List<MultipartFile> images) throws IOException {
		List<String> imageUrls = new ArrayList<>();
		if (images == null) {
			return imageUrls;
		}

		for (MultipartFile image : images) {
			imageUrls.add(uploadImage(image));
		}
		return imageUrls;
	}

	public String uploadImage(MultipartFile image) throws IOException {
		validateImageIsEmpty(image);

		String uuidFileName =
				FileUtils.generateUUIDFileName(Objects.requireNonNull(image.getOriginalFilename()));
		String ext = image.getContentType();
		String imageUrl = "https://storage.googleapis.com/" + bucketName + "/" + uuidFileName;
		BlobInfo blobInfo =
				BlobInfo.newBuilder(bucketName, uuidFileName).setContentType(ext).build();

		storage.create(blobInfo, image.getInputStream());

		return imageUrl;
	}

	private void validateImageIsEmpty(MultipartFile image) {
		if (image.isEmpty()) {
			throw new FileNotFoundException(ErrorCode.FILE_NOT_FOUND);
		}
	}
}
