package org.kokoatalkserver.domain.member.entity;

public enum Role {
    MEMBER("일반 회원"),
    ADMIN("관리자");
    private String description;

    Role(String description) {
        this.description = description;
    }
}
