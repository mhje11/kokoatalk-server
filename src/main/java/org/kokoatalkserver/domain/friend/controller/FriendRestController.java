package org.kokoatalkserver.domain.friend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.kokoatalkserver.domain.friend.dto.FriendInfoDto;
import org.kokoatalkserver.domain.friend.dto.FriendSearchDto;
import org.kokoatalkserver.domain.friend.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendRestController {
    private final FriendService friendService;

    //친구검색
    @GetMapping("/search")
    public ResponseEntity<FriendInfoDto> findFriend(@RequestBody FriendSearchDto friendSearchDto) {
        FriendInfoDto friendInfoDto = friendService.findByFriendCode(friendSearchDto.getFriendCode());
        return ResponseEntity.ok(friendInfoDto);
    }

    //친구추가
    @PostMapping("/add")
    public ResponseEntity<String> addFriend(String friendCode, HttpServletRequest request) {
        friendService.addFriend(request, friendCode);
        return ResponseEntity.ok("친구 추가 완료, 친구 코드 : " + friendCode);
    }

    //친구리스트
    @GetMapping("/friendList")
    public ResponseEntity<List<FriendInfoDto>> findFriendList(HttpServletRequest request) {
        List<FriendInfoDto> friendList = friendService.getFriendList(request);
        return ResponseEntity.ok(friendList);
    }

}
