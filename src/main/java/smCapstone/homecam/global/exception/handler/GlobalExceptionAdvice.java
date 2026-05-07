package smCapstone.homecam.global.exception.handler;

import org.springframework.web.bind.MethodArgumentNotValidException;
import smCapstone.homecam.global.apipayload.BaseErrorCode;
import smCapstone.homecam.global.apipayload.GeneralErrorCode;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    // 커스텀 예외 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(
            GeneralException ex
    ) {
        log.warn("[ CustomException ]: {}", ex.getCode().getMessage());
        ApiResponse<Object> errorResponse = ApiResponse.onFailure(ex.getCode(), null);
        return ResponseEntity
                .status(ex.getCode().getStatus())
                .body(errorResponse);
    }

    // @Valid 유효성 검사 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        // 여러 에러 중 첫 번째 메시지 추출
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("[ Validation Error ]: {}", errorMessage);

        BaseErrorCode errorCode = GeneralErrorCode.BAD_REQUEST_400; // 프로젝트 내 400 에러 코드
        ApiResponse<Object> errorResponse = ApiResponse.onFailure(errorCode, errorMessage); // result에 에러 메시지 담기

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorResponse);
    }

    // 나머지 모든 예외 처리
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse<Object>> handleAllException(
            Exception ex
    ) {
        log.error("[WARNING] Internal Server Error : {} ", ex.getMessage());
        BaseErrorCode errorCode = GeneralErrorCode.INTERNAL_SERVER_ERROR_500;
        ApiResponse<Object> errorResponse = ApiResponse.onFailure(errorCode, null);
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorResponse);
    }
}
