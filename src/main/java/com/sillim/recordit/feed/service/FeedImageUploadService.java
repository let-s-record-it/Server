package com.sillim.recordit.feed.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sillim.recordit.feed.domain.Feed;
import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.FileNotFoundException;
import com.sillim.recordit.global.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
public class FeedImageUploadService {

	private final FeedQueryService feedQueryService;
	private final AmazonS3 amazonS3;
	private final String endpoint;

	public FeedImageUploadService(
			FeedQueryService feedQueryService,
			AmazonS3 amazonS3,
			@Value("${cloud.aws.s3.endpoint}") String endpoint) {
		this.feedQueryService = feedQueryService;
		this.amazonS3 = amazonS3;
		this.endpoint = endpoint;
	}

	public void upload(List<MultipartFile> images, Long feedId) throws IOException {
		Feed feed = feedQueryService.searchById(feedId);
		List<String> feedImageUrls = new ArrayList<>();
		for (MultipartFile image : images) {
			feedImageUrls.add(upload(image));
		}
		feed.setFeedImages(feedImageUrls);
	}

	public String upload(MultipartFile image) throws IOException {
		validateImageIsEmpty(image);
		String imagePath = FileUtils.toImagePath(image.getOriginalFilename());
		File convertImageFile = FileUtils.convert(image);

		amazonS3.putObject(
				new PutObjectRequest(endpoint, imagePath, convertImageFile)
						.withCannedAcl(CannedAccessControlList.PublicRead));
		deleteImageFile(convertImageFile);

		return amazonS3.getUrl(endpoint, image.getOriginalFilename()).toString();
	}

	private static void deleteImageFile(File convertImageFile) {
		if (convertImageFile.delete()) {
			log.info("[FeedImageUploadService.upload] 임시 파일이 삭제되었습니다.");
		} else {
			log.info("[FeedImageUploadService.upload] 임시 파일을 삭제하지 못했습니다.");
		}
	}

	private void validateImageIsEmpty(MultipartFile image) {
		if (image.isEmpty()) {
			throw new FileNotFoundException(ErrorCode.FILE_NOT_FOUND);
		}
	}
}
