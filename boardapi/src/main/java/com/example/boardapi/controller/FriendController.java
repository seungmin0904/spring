package com.example.boardapi.controller;

import com.example.boardapi.dto.FriendDTO;
import com.example.boardapi.security.dto.MemberSecurityDTO;
import com.example.boardapi.service.FriendService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 1. 친구 신청
    @PostMapping
    public void requestFriend(@RequestBody FriendDTO.Request dto,
            @AuthenticationPrincipal MemberSecurityDTO principal) {
        friendService.requestFriend(principal.getMno(), dto.getTargetMemberId());
    }

    // 2. 친구 수락
    @PostMapping("/{friendId}/accept")
    public void acceptFriend(@PathVariable Long friendId,
            @AuthenticationPrincipal MemberSecurityDTO principal) {
        friendService.acceptFriend(friendId, principal.getMno());
    }

    // 3. 친구 거절 (옵션)
    @PostMapping("/{friendId}/reject")
    public void rejectFriend(@PathVariable Long friendId,
            @AuthenticationPrincipal MemberSecurityDTO principal) {
        friendService.rejectFriend(friendId, principal.getMno());
    }

    // 4. 내 친구 목록 (SimpleResponse로 변경)
    @GetMapping
    public List<FriendDTO.SimpleResponse> getFriends(@AuthenticationPrincipal MemberSecurityDTO member) {
        return friendService.getFriends(member.getMno());
    }

}
