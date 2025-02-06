package com.backstage.curtaincall.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    private static final String BUCKET_NAME = "concert-ticket-images";

    public String uploadFile(String fileName, InputStream inputStream, long fileSize) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, fileSize));

        return String.format("https://%s.s3.amazonaws.com/%s", BUCKET_NAME, fileName);
    }

}
