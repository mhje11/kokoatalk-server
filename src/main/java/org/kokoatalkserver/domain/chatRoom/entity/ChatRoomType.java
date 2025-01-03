package org.kokoatalkserver.domain.chatRoom.entity;

public enum ChatRoomType {
    PRIVATE("개인 채팅방"),
    GROUP("그룹 채팅방");
    private String description;

    ChatRoomType(String description) {
        this.description = description;
    }
}
