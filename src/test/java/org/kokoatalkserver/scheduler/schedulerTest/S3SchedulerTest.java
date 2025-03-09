package org.kokoatalkserver.scheduler.schedulerTest;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kokoatalkserver.domain.chatMessage.scheduler.S3Scheduler;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3SchedulerTest {

    @InjectMocks
    private S3Scheduler s3Scheduler;

    @Mock
    private AmazonS3 amazonS3;

    @Test
    void 임시파일_삭제_스케줄러_실행_테스트() {
        ReflectionTestUtils.setField(s3Scheduler, "bucket", "test-bucket");

        ListObjectsV2Result mockResult = mock(ListObjectsV2Result.class);
        when(mockResult.getObjectSummaries()).thenReturn(Collections.emptyList());

        when(amazonS3.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockResult);

        ReflectionTestUtils.invokeMethod(s3Scheduler, "deleteUnusedTempFiles");

        verify(amazonS3, times(1)).listObjectsV2(any(ListObjectsV2Request.class));
        verify(amazonS3, atLeast(0)).deleteObject(anyString(), anyString());
    }
}
