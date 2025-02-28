package org.kokoatalkserver.domain.member.repository;

import org.kokoatalkserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByFriendCode(String friendCode);
    List<Member> findByFriendCodeIn(List<String> friendCode);

}
