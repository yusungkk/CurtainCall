package com.backstage.curtaincall.image;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET_NAME;

    // 이미지 등록
    public String uploadFile(String OriginalfileName, InputStream inputStream, long fileSize) {
        // 중복 방지
        String fileName = UUID.randomUUID() + "_" + OriginalfileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ)  // 퍼블릭 읽기 설정 추가
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, fileSize));

        return String.format("https://%s.s3.amazonaws.com/%s", BUCKET_NAME, fileName);
    }

    // 이미지 삭제
    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}
