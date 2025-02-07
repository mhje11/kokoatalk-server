package org.kokoatalkserver.domain.friend.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.friend.entity.Friend;
import org.kokoatalkserver.domain.friend.repository.FriendRepository;
import org.kokoatalkserver.domain.friend.dto.FriendInfoDto;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.kokoatalkserver.global.util.jwt.service.CustomUserDetails;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public FriendInfoDto findByFriendCode(String friendCode) {
        Optional<Member> optionalMember = memberRepository.findByFriendCode(friendCode);
        Member friend = optionalMember.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        return FriendInfoDto.fromMember(friend);
    }

    @Transactional
    public void addFriend(CustomUserDetails customUserDetails, String friendCode) {
        String accountId = customUserDetails.getUserId();
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        Optional<Member> friendOptional = memberRepository.findByFriendCode(friendCode);
        Member friend = friendOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (member.equals(friend)) {
            throw new CustomException(ExceptionCode.CANNOT_ADD_SELF);
        }

        if (friendRepository.existsByMemberAndFriend(member, friend)) {
            throw new CustomException(ExceptionCode.ALREADY_ADDED_FRIEND);
        }

        Friend friendEntity = Friend.builder()
                .member(member)
                .friend(friend)
                .build();

        friendRepository.save(friendEntity);
    }

    public List<FriendInfoDto> getFriendList(CustomUserDetails customUserDetails) {
        Optional<Member> memberOptional = memberRepository.findByLoginId(customUserDetails.getUserId());
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        List<Friend> friendList = friendRepository.findFriendsWithMembers(member.getKokoaId());

        return friendList.stream()
                .map(friend -> FriendInfoDto.fromMember(friend.getFriend()))
                .collect(Collectors.toList());
    }
}
