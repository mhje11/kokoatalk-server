package org.kokoatalkserver.domain.chatMessage.scheduler;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Scheduler {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Scheduled(cron = "0 0 3 * * *")
    public void deleteUnusedTempFiles() {
        log.info("임시 파일 삭제");

        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix("temp/");

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        result.getObjectSummaries().forEach(s3object -> {
            Date lastModified = s3object.getLastModified();
            if (isOlderThan24Hours(lastModified)) {
                amazonS3.deleteObject(bucket, s3object.getKey());
                log.info("24시간 동안 사용되지 않은 임시파일 삭제 : {}", s3object.getKey());
            }
        });
    }

    private boolean isOlderThan24Hours(Date lastModified) {
        long currentTime = System.currentTimeMillis();
        long fileTime = lastModified.getTime();
        return (currentTime - fileTime) > (24 * 60 * 60 * 1000);
    }
}
