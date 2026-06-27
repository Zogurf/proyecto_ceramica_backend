package com.example.backend.controllers;

import com.example.backend.dto.UploadImageResponse;
import com.example.backend.services.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/uploads")
@RequiredArgsConstructor
public class UploadController {
    private final S3StorageService s3StorageService;

    @PostMapping("/images")
    public UploadImageResponse uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "productos") String folder
    ) {
        return s3StorageService.uploadImage(file, folder);
    }
}
