package org.kokoatalkserver.domain.member.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.domain.s3.service.S3Service;
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
    private final S3Service s3Service;

    public Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(() ->
                new CustomException(ExceptionCode.ID_MISSMATCH));
    }

    @Transactional
    public void uploadProfileImage(MultipartFile multipartFile, String accountId) {
        String profileUrl = s3Service.uploadFile(multipartFile, "profile/");
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.getProfileUrl().equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png")) {
            s3Service.deleteFileByUrl(member.getProfileUrl());
        }
        member.updateProfileUrl(profileUrl);
        memberRepository.save(member);
    }

    @Transactional
    public void uploadBackgroundImage(MultipartFile multipartFile, String accountId) {
        String backgroundUrl = s3Service.uploadFile(multipartFile, "background/");

        Member member = memberRepository.findByLoginId(accountId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (!member.getBackgroundUrl().equals("https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg")) {
            s3Service.deleteFileByUrl(member.getBackgroundUrl());
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
        s3Service.deleteFileByUrl(profileUrl);
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
        s3Service.deleteFileByUrl(backgroundUrl);
        member.deleteBackgroundImage();
        memberRepository.save(member);
    }



    @Transactional
    public void updateBio(String bio, String accountId) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        member.updateBio(bio);
        memberRepository.save(member);
    }


}
