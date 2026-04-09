package smCapstone.homecam.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import smCapstone.homecam.global.apipayload.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "사용자를 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER4002", "이미 존재하는 사용자 아이디입니다."),
    INVALID_CHECK_CODE(HttpStatus.BAD_REQUEST, "AUTH4003", "인증번호가 일치하지 않거나 만료되었습니다."),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH5001", "이메일 발송 중 오류가 발생했습니다.");

    private final HttpStatus Status;
    private final String code;
    private final String message;

}
