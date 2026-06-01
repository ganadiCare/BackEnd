package smCapstone.homecam.domain.device.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import smCapstone.homecam.global.apipayload.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum CameraErrorCode implements BaseErrorCode {

    CAMERA_NOT_FOUND(HttpStatus.NOT_FOUND, "CAM4001", "카메라를 찾을 수 없습니다."),
    CAMERA_ALREADY_EXISTS(HttpStatus.CONFLICT, "CAM4002", "이미 등록된 카메라가 있습니다."),
    CAMERA_NOT_OWNED(HttpStatus.FORBIDDEN, "CAM4003", "해당 카메라에 대한 권한이 없습니다."),
    RECORDING_NOT_FOUND(HttpStatus.NOT_FOUND, "CAM4004", "녹화/캡처 파일을 찾을 수 없습니다."),
    FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CAM5001", "파일 저장에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CAM5002", "파일 삭제에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
