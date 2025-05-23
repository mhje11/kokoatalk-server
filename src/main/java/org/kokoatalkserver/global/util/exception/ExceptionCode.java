package org.kokoatalkserver.global.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {
    /**
     * 유저 관련
     */
    MEMBER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ID_MISSMATCH(401, HttpStatus.UNAUTHORIZED, "아이디가 일치하지 않습니다."),
    PASSWORD_MISMATCH(401, HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_USER_ID(409, HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
    VALIDATION_FAILED(400, HttpStatus.BAD_REQUEST, "유효하지 않은 형식입니다."),
    CANNOT_ADD_SELF(400, HttpStatus.BAD_REQUEST, "자기 자신을 친구로 추가 할 수 없습니다."),
    ALREADY_ADDED_FRIEND(400, HttpStatus.BAD_REQUEST, "이미 추가된 친구 입니다."),


    /**
     * 토근 관련
     */
    JWT_TOKEN_EXPIRED(401, HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    ACCESS_DENIED(403, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NO_LOGIN(401, HttpStatus.UNAUTHORIZED, "로그인후 이용 가능합니다."),
    INVALID_REFRESH_TOKEN(401, HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(404, HttpStatus.NOT_FOUND, "토큰이 존재하지 않습니다."),
    /**
     * 채팅방 관련
     */
    CHAT_ROOM_NOT_FOUND(404, HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CHATROOM_FULL(409, HttpStatus.CONFLICT, "채팅방이 가득 찼습니다."),
    CHAT_ROOM_PARTICIPANT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "채팅방에 인원이 존재하지 않습니다."),


    /**
     * 파일 업로드 관련
     */
    FILE_UPLOAD_FAILED(500, HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(500, HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    INVALID_FILE_FORMAT(400, HttpStatus.BAD_REQUEST, "유효하지 않은 파일 형식입니다."),
    INVALID_FILE_URL(400, HttpStatus.BAD_REQUEST, "잘못된 파일 URL입니다."),
    CANNOT_DELETE_DEFAULT_IMAGE(400, HttpStatus.BAD_REQUEST, "기본 프로필 또는 배경 이미지는 삭제할 수 없습니다."),
    IMAGE_URL_EXPIRATION(4000, HttpStatus.BAD_REQUEST, "유효하지 않은 URL 입니다. 다시 업로드 해주세요"),


    /**
     * 서버 관련
     */
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_PATH(404, HttpStatus.NOT_FOUND, "올바르지 않은 경로입니다."),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ExceptionCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
