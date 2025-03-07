package org.kokoatalkserver.domain.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * S3에 파일 업로드 (임시 저장)
     */
    public String uploadFileToTemp(MultipartFile multipartFile) {
        return uploadFile(multipartFile, "temp/");
    }

    /**
     * S3에 파일 업로드 (최종 저장)
     */
    public String uploadFile(MultipartFile multipartFile, String path) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_FILE_FORMAT);
        }

        String fileName = path + createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.FILE_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /**
     * 파일을 최종 저장소로 이동
     */
    public String moveFileToFinalLocation(String tempUrl) {
        String tempPath = tempUrl.substring(tempUrl.indexOf("/temp") + 1);
        String finalPath = tempPath.replace("temp/", "chat/");

        try {
            amazonS3.copyObject(bucket, tempPath, bucket, finalPath);
            amazonS3.deleteObject(bucket, tempPath);
        } catch (AmazonS3Exception e) {
            throw new CustomException(ExceptionCode.FILE_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, finalPath).toString();
    }

    /**
     * S3에서 파일 삭제
     */
    public void deleteFileByUrl(String fileUrl) {
        if (!fileUrl.contains(bucket)) {
            throw new CustomException(ExceptionCode.INVALID_FILE_URL);
        }

        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
            log.info("S3에서 파일 삭제 : " + fileName);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * 파일 이름을 UUID 기반으로 생성
     */
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 : " + fileName + " 입니다.");
        }
    }
}
