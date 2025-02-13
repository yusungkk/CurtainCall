package com.backstage.curtaincall.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class FileUploadController {
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl;
        try (InputStream inputStream = file.getInputStream()) {
            fileUrl = s3Service.uploadFile(file.getOriginalFilename(), inputStream, file.getSize());
        }
        return ResponseEntity.ok(fileUrl);
    }
}
