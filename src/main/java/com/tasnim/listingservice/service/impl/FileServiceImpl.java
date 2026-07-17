package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.BadRequestException;
import com.tasnim.commonlibrary.exceptions.BusinessException;
import com.tasnim.commonlibrary.utils.SecurityUtil;
import com.tasnim.listingservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

import static com.tasnim.listingservice.utils.Constants.ALLOWED_TYPES;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Value("${file.max-size:1}")
    private long MAX_FILE_SIZE;
    @Value("${file.max-uploads:2}")
    private int MAX_UPLOADS;

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        log.info("Uploading {} image(s)", files == null ? 0 : files.size());

        validateFiles(files);
        String userId = SecurityUtil.getCurrentUserId();

        List<String> urls = files.stream()
                .map(file -> uploadToStorage(file, userId))
                .toList();

        log.info("Successfully uploaded {} image(s)", urls.size());

        return urls;
    }

    private String uploadToStorage(MultipartFile file,  String userId) {
        try {
            String fileName = getFileName(file, userId);
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);
            Path destination = uploadDir.resolve(fileName);
            file.transferTo(destination);

            return "/uploads/" + fileName;
        } catch (IOException ex) {
            log.error("Failed to upload image. file={}", file.getOriginalFilename(), ex);
            throw new BusinessException(
                    "Failed to upload image");
        }
    }

    private String getFileName(MultipartFile file, String userId) {
        return userId + "_" +
                Instant.now().toEpochMilli() + "_" +
                file.getOriginalFilename();
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException(
                    "At least one image is required");
        }
        if (files.size() > MAX_UPLOADS) {
            throw new BadRequestException(
                    "Maximum " + MAX_UPLOADS + " images allowed");
        }

        files.forEach(this::validateFile);
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE * 1024 * 1024) {
            throw new BadRequestException(
                    "File size exceeds 5 MB");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException(
                    "Unsupported file type");
        }
    }
}
