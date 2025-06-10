package com.example.boardapi.dto;

import java.time.LocalDateTime;

import com.example.boardapi.entity.Friend;
import com.example.boardapi.entity.FriendStatus;
import com.example.boardapi.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class FriendDTO {

    @Data
    public static class Request {
        private Long targetMemberId; // 친구 신청 대상
    }

    // 프론트에 내려줄 단순 DTO (상대방만!)
    @Data
    public static class SimpleResponse {
        private Long friendId; // 친구관계 PK
        private Long memberId; // 상대방 PK
        private String name; // 상대방 이름
        private String profile; // (프로필 경로 등)
        // 필요하면 status 등도 추가

        public static SimpleResponse from(Friend f, Long myId) {
            SimpleResponse dto = new SimpleResponse();
            dto.setFriendId(f.getId());
            if (f.getMemberA().getMno().equals(myId)) {
                dto.setMemberId(f.getMemberB().getMno());
                dto.setName(f.getMemberB().getName());
                dto.setProfile(f.getMemberB().getProfile());
            } else {
                dto.setMemberId(f.getMemberA().getMno());
                dto.setName(f.getMemberA().getName());
                dto.setProfile(f.getMemberA().getProfile());
            }
            return dto;
        }
    }

    // 친구 신청 요청 응답
    @Getter
    @AllArgsConstructor
    public static class RequestResponse {
        private Long requestId;
        private Long requesterId;
        private String requesterNickname;
        private Long receiverId;
        private String receiverNickname;
        private LocalDateTime requestTime;

        public static RequestResponse from(Friend friend) {
            return new RequestResponse(
                    friend.getId(),
                    friend.getMemberA().getMno(),
                    friend.getMemberA().getName(),
                    friend.getMemberB().getMno(),
                    friend.getMemberB().getName(),
                    friend.getCreatedAt());
        }
    }

    // 상태 응답
    @Data
    @AllArgsConstructor
    public static class StatusResponse {
        private FriendStatus status;
    }

}