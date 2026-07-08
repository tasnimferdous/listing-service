package com.tasnim.listingservice.service.impl;

import com.tasnim.commonlibrary.exceptions.BadRequestException;
import com.tasnim.commonlibrary.exceptions.BusinessException;
import com.tasnim.listingservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {
        log.info("Uploading {} image(s)", files == null ? 0 : files.size());

        validateFiles(files);
        List<String> urls = files.stream()
                .map(this::uploadToStorage)
                .toList();

        log.info("Successfully uploaded {} image(s)", urls.size());

        return urls;
    }

    private String uploadToStorage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
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

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException(
                    "At least one image is required");
        }
        if (files.size() > 10) {
            throw new BadRequestException(
                    "Maximum 10 images allowed");
        }

        files.forEach(this::validateFile);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException(
                    "File cannot be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(
                    "File size exceeds 5 MB");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BadRequestException(
                    "Unsupported file type");
        }
    }
}
