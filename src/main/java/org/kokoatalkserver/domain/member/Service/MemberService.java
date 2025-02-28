package org.kokoatalkserver.domain.member.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(() ->
                new CustomException(ExceptionCode.ID_MISSMATCH));
    }

    @Transactional
    public void uploadProfileImage(MultipartFile multipartFile, String accountId) {
        String profileUrl = uploadFile(multipartFile);
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.getProfileUrl().equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png")) {
            deleteFileByUrl(member.getProfileUrl());
        }
        member.updateProfileUrl(profileUrl);
        memberRepository.save(member);
    }

    @Transactional
    public void uploadBackgroundImage(MultipartFile multipartFile, String accountId) {
        String backgroundUrl = uploadFile(multipartFile);
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        if (!member.getBackgroundUrl().equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg")) {
            deleteFileByUrl(member.getBackgroundUrl());
        }
        member.updateBackgroundUrl(backgroundUrl);
        memberRepository.save(member);
    }

    @Transactional
    public void deleteProfileImage(String accountId) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        String profileUrl = member.getProfileUrl();
        if (profileUrl.equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png")) {
            throw new CustomException(ExceptionCode.CANNOT_DELETE_DEFAULT_IMAGE);
        }
        deleteFileByUrl(profileUrl);
        member.deleteProfileImage();
        memberRepository.save(member);
    }

    @Transactional
    public void deleteBackgroundImage(String accountId) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        String backgroundUrl = member.getBackgroundUrl();
        if (backgroundUrl.equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg")) {
            throw new CustomException(ExceptionCode.CANNOT_DELETE_DEFAULT_IMAGE);
        }
        deleteFileByUrl(backgroundUrl);
        member.deleteBackgroundImage();
        memberRepository.save(member);
    }

    private String uploadFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(ExceptionCode.INVALID_FILE_FORMAT);
        }

        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.FILE_UPLOAD_FAILED);
        }
        return amazonS3.getUrl(bucket, fileName).toString();
    }


    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 : " + fileName + " 입니다.");
        }
    }

    private void deleteFileByUrl(String fileUrl) {
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

    @Transactional
    public void updateBio(String bio, String accountId) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        member.updateBio(bio);
        memberRepository.save(member);
    }


}
