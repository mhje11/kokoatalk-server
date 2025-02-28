package org.kokoatalkserver.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "kokoa_id")
    private Long kokoaId;

    @Column(name = "friend_code", unique = true)
    private String friendCode;

    @Column(nullable = false, unique = true, name = "login_id")
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    private String bio;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "background_url")
    private String backgroundUrl;

    @Column(name = "remember_me")
    private Boolean rememberMe;


    @Builder
    public Member(String loginId, String password, String bio, String profileUrl, String backgroundUrl, String nickname, Boolean rememberMe) {
        this.loginId = loginId;
        this.password = password;
        this.role = Role.MEMBER;
        this.friendCode = loginId;
        this.bio = bio != null ? bio : "상태 메시지를 적용해보세요";
        this.profileUrl = profileUrl != null ? profileUrl : "https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png";
        this.backgroundUrl = backgroundUrl != null ? backgroundUrl : "https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg";
        this.nickname = nickname;
        this.rememberMe = rememberMe != null ? rememberMe : false;
    }



    public void rememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public void updateProfileUrl(String profileUrl) {
        if (profileUrl == null || profileUrl.isBlank()) {
            throw new IllegalArgumentException("프로필 이미지는 비어 있을 수 없습니다.");
        }
        this.profileUrl = profileUrl;
    }
    public void updateBackgroundUrl(String backgroundUrl) {
        if (backgroundUrl == null || backgroundUrl.isBlank()) {
            throw new IllegalArgumentException("배경 이미지는 비어 있을 수 없습니다.");
        }
        this.backgroundUrl = backgroundUrl;
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void deleteProfileImage() {
        this.profileUrl = "https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_default_image.png";
    }

    public void deleteBackgroundImage() {
        this.backgroundUrl = "https://kokoatalk-bucket.s3.ap-northeast-2.amazonaws.com/kokoatalk_background.jpg";
    }

}
