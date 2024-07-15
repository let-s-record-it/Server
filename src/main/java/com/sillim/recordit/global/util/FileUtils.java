package com.sillim.recordit.global.util;

import com.sillim.recordit.global.exception.ErrorCode;
import com.sillim.recordit.global.exception.common.FileGenerateException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public final class FileUtils {

    private static final String IMAGE_BASE_URL = "images/";

    public static String generateUUIDFileName(String fileName) {
        return UUID.randomUUID() + "_" + fileName.replaceAll("\\s", "_");
    }

    public static String toImagePath(String fileName) {
        return IMAGE_BASE_URL + fileName;
    }

    public static File convert(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        File convertFile = new File(Objects.requireNonNull(fileName));
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                log.error("[FileUtils.convert] 파일 변환 오류: {}", e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new FileGenerateException(ErrorCode.FILE_GENERATE_FAIL);
    }
}
