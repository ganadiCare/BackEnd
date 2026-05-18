package smCapstone.homecam.domain.device.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smCapstone.homecam.domain.device.dto.request.CameraRequestDTO;
import smCapstone.homecam.domain.device.dto.response.CameraResponseDTO;
import smCapstone.homecam.domain.device.service.command.CameraCommandService;
import smCapstone.homecam.domain.device.service.query.CameraQueryService;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cameras")
@Tag(name = "Camera API", description = "카메라 설정 관리 API")
public class CameraController {

    private final CameraCommandService cameraCommandService;
    private final CameraQueryService cameraQueryService;

    @GetMapping
    @Operation(summary = "내 카메라 조회 API")
    public ApiResponse<CameraResponseDTO.CameraDTO> getMyCamera() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, cameraQueryService.getMyCamera(SecurityUtil.getCurrentMemberId()));
    }

    @PatchMapping
    @Operation(summary = "카메라 설정 수정 API", description = "디바이스명, 야간모드, 프라이빗 모드 수정")
    public ApiResponse<CameraResponseDTO.CameraDTO> updateCamera(@RequestBody CameraRequestDTO.UpdateCameraDTO request) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, cameraCommandService.updateCamera(SecurityUtil.getCurrentMemberId(), request));
    }

    @DeleteMapping
    @Operation(summary = "카메라 연결 해제 API")
    public ApiResponse<String> deleteCamera() {
        cameraCommandService.deleteCamera(SecurityUtil.getCurrentMemberId());
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "카메라 연결이 해제되었습니다.");
    }
}
