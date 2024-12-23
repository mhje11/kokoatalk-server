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

    @Column(name = "friend_code")
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


    @Builder
    public Member(String loginId, String password, String bio, String profileUrl, String backgroundUrl, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.role = Role.MEMBER;
        this.friendCode = loginId;
        this.bio = bio != null ? bio : "상태 메시지를 적용해보세요";
        this.profileUrl = profileUrl != null ? profileUrl : "http://default.com/profile.jpg";
        this.backgroundUrl = backgroundUrl != null ? backgroundUrl : "http://default.com/background.jpg";
        this.nickname = nickname;
    }


    public static Member createMember(String loginId, String password, Role role) {
        Member member = new Member();
        member.loginId = loginId;
        member.password = password;
        member.role = Role.MEMBER;
        return member;
    }

}
