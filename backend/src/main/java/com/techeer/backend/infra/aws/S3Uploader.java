package com.techeer.backend.infra.aws;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadPdf(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String s3PdfName = UUID.randomUUID().toString().substring(0, 10) + "_" + originalFileName;
        String s3Key = "files/" + s3PdfName;
        String fileUrl = uploadToS3(multipartFile, s3Key);
        log.info("file_url length: {}", fileUrl.length());
        return fileUrl;
    }

    private String uploadToS3(MultipartFile multipartFile, String s3Key) {
        try {
            ObjectMetadata metadata = createObjectMetadata(multipartFile);
            amazonS3.putObject(new PutObjectRequest(bucket, s3Key, multipartFile.getInputStream(), metadata));
            return amazonS3.getUrl(bucket, s3Key).toString();
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }

    private File convertMultipartFileToLocalFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            log.error("Failed to convert MultipartFile to File", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return file;
    }

    private void deleteLocalFile(File file) {
        if (!file.delete()) {
            log.error("Failed to delete local file");
        }
    }

    public void delete(String fileName) {
        amazonS3.deleteObject(bucket, fileName);
    }

    public InputStream getFileFromS3(String bucketName, String key) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            log.info("Successfully retrieved S3 object. Content length: {}",
                    s3Object.getObjectMetadata().getContentLength());
            return s3Object.getObjectContent();
        } catch (Exception e) {
            log.error("Error retrieving file from S3. Bucket: '{}', Key: '{}'", bucketName, key, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String getBucketName() {
        return bucket;
    }

}