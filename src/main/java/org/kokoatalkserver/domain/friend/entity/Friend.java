package org.kokoatalkserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.kokoatalkserver.domain.friend.dto.FriendInfoDto;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kokoa_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_kokoa_id")
    private Member friend;

    @Builder
    private Friend(Member member, Member friend) {
        this.member = member;
        this.friend = friend;
    }

    public static Friend createFriendEntity(Member member, Member friend) {
        return Friend.builder()
                .member(member)
                .friend(friend)
                .build();
    }
}
