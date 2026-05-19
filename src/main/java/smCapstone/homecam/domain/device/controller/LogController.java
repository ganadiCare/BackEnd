package smCapstone.homecam.domain.device.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smCapstone.homecam.domain.device.dto.request.LogRequestDTO;
import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;
import smCapstone.homecam.domain.device.service.command.LogCommandService;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
@Tag(name = "Log API", description = "급식/급수 로그 기록 API (라즈베리파이 연동)")
public class LogController {

    private final LogCommandService logCommandService;

    @PostMapping("/feeding")
    @Operation(summary = "급식 로그 기록 API", description = "라즈베리파이에서 급식 후 로그를 저장합니다.")
    public ApiResponse<DispenserResponseDTO.FeedingLogDTO> createFeedingLog(
            @RequestBody @Valid LogRequestDTO.CreateFeedingLogDTO request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED,
                logCommandService.createFeedingLog(SecurityUtil.getCurrentMemberId(), request));
    }

    @PostMapping("/watering")
    @Operation(summary = "급수 로그 기록 API", description = "라즈베리파이에서 급수 후 로그를 저장합니다.")
    public ApiResponse<DispenserResponseDTO.WateringLogDTO> createWateringLog(
            @RequestBody @Valid LogRequestDTO.CreateWateringLogDTO request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED,
                logCommandService.createWateringLog(SecurityUtil.getCurrentMemberId(), request));
    }
}
