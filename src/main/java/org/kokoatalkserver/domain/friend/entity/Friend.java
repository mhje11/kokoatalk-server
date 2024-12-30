package org.kokoatalkserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kokoatalkserver.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kokoa_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_kokoa_id")
    private Member friend;

    @Builder
    public Friend(Member member, Member friend) {
        this.member = member;
        this.friend = friend;
    }
}
