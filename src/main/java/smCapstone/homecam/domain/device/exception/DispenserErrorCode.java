package smCapstone.homecam.domain.device.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import smCapstone.homecam.global.apipayload.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum DispenserErrorCode implements BaseErrorCode {

    DISPENSER_NOT_FOUND(HttpStatus.NOT_FOUND, "DSP4001", "디스펜서를 찾을 수 없습니다."),
    DISPENSER_NOT_OWNED(HttpStatus.FORBIDDEN, "DSP4003", "해당 디스펜서에 대한 권한이 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "DSP4004", "급식 스케줄을 찾을 수 없습니다."),
    SCHEDULE_NOT_OWNED(HttpStatus.FORBIDDEN, "DSP4005", "해당 스케줄에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
