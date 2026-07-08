package com.tasnim.listingservice.controller;

import com.tasnim.commonlibrary.model.CommonResponse;
import com.tasnim.commonlibrary.utils.ResponseUtil;
import com.tasnim.listingservice.service.FileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public CommonResponse<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> urls = fileService.uploadImages(files);
        return ResponseUtil.success(urls, "Images uploaded successfully");
    }
}