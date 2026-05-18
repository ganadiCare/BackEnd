package smCapstone.homecam.domain.pet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import smCapstone.homecam.global.apipayload.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum PetErrorCode implements BaseErrorCode {

    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "PET4001", "반려동물을 찾을 수 없습니다."),
    PET_NOT_OWNED(HttpStatus.FORBIDDEN, "PET4003", "해당 반려동물에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
