package org.kokoatalkserver.domain.friend.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.friend.dto.FriendSearchDto;
import org.kokoatalkserver.domain.friend.entity.Friend;
import org.kokoatalkserver.domain.friend.repository.FriendRepository;
import org.kokoatalkserver.domain.friend.dto.FriendInfoDto;
import org.kokoatalkserver.domain.member.entity.Member;
import org.kokoatalkserver.domain.member.repository.MemberRepository;
import org.kokoatalkserver.global.util.exception.CustomException;
import org.kokoatalkserver.global.util.exception.ExceptionCode;
import org.kokoatalkserver.global.util.jwt.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    public FriendInfoDto findByFriendCode(@RequestBody FriendSearchDto friendSearchDto) {
        Optional<Member> optionalMember = memberRepository.findByFriendCode(friendSearchDto.getFriendCode());
        Member friend = optionalMember.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        return FriendInfoDto.toDto(friend);
    }

    @Transactional
    public void addFriend(HttpServletRequest request, String friendCode) {
        String myAccountId = refreshTokenService.getAccountId(request);
        Optional<Member> memberOptional = memberRepository.findByLoginId(myAccountId);
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

    public List<FriendInfoDto> getFriendList(HttpServletRequest request) {
        String accountId = refreshTokenService.getAccountId(request);
        Optional<Member> memberOptional = memberRepository.findByLoginId(accountId);
        Member member = memberOptional.orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        List<Friend> friendList = friendRepository.findAllByMember(member);

        return friendList.stream()
                .map(friend -> FriendInfoDto.toDto(friend.getFriend()))
                .collect(Collectors.toList());
    }
}
