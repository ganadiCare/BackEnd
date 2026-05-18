package smCapstone.homecam.domain.device.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import smCapstone.homecam.domain.device.dto.request.DispenserRequestDTO;
import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;
import smCapstone.homecam.domain.device.service.command.DispenserCommandService;
import smCapstone.homecam.domain.device.service.query.DispenserQueryService;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dispensers")
@Tag(name = "Dispenser API", description = "사료통 설정 관리 API")
public class DispenserController {

    private final DispenserCommandService dispenserCommandService;
    private final DispenserQueryService dispenserQueryService;

    @GetMapping
    @Operation(summary = "내 디스펜서 조회 API")
    public ApiResponse<DispenserResponseDTO.DispenserDTO> getMyDispenser() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, dispenserQueryService.getMyDispenser(SecurityUtil.getCurrentMemberId()));
    }

    @PatchMapping
    @Operation(summary = "디스펜서 설정 수정 API")
    public ApiResponse<DispenserResponseDTO.DispenserDTO> updateDispenser(
            @RequestBody @Valid DispenserRequestDTO.UpdateDispenserDTO request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, dispenserCommandService.updateDispenser(SecurityUtil.getCurrentMemberId(), request));
    }

    @DeleteMapping
    @Operation(summary = "디스펜서 연결 해제 API")
    public ApiResponse<String> deleteDispenser() {
        dispenserCommandService.deleteDispenser(SecurityUtil.getCurrentMemberId());
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "디스펜서 연결이 해제되었습니다.");
    }

    // 급식 스케줄
    @PostMapping("/schedules")
    @Operation(summary = "급식 스케줄 등록 API")
    public ApiResponse<DispenserResponseDTO.ScheduleDTO> createSchedule(
            @RequestBody @Valid DispenserRequestDTO.CreateScheduleDTO request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, dispenserCommandService.createSchedule(SecurityUtil.getCurrentMemberId(), request));
    }

    @PatchMapping("/schedules/{scheduleId}")
    @Operation(summary = "급식 스케줄 수정 API")
    public ApiResponse<DispenserResponseDTO.ScheduleDTO> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody @Valid DispenserRequestDTO.UpdateScheduleDTO request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, dispenserCommandService.updateSchedule(SecurityUtil.getCurrentMemberId(), scheduleId, request));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    @Operation(summary = "급식 스케줄 삭제 API")
    public ApiResponse<String> deleteSchedule(@PathVariable Long scheduleId) {
        dispenserCommandService.deleteSchedule(SecurityUtil.getCurrentMemberId(), scheduleId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "급식 스케줄이 삭제되었습니다.");
    }

    // 로그 조회 (GPT 보고서용)
    @GetMapping("/logs/feeding")
    @Operation(summary = "급식 로그 조회 API", description = "기간별 급식 로그 조회 (GPT 보고서용)")
    public ApiResponse<List<DispenserResponseDTO.FeedingLogDTO>> getFeedingLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, dispenserQueryService.getFeedingLogs(SecurityUtil.getCurrentMemberId(), from, to));
    }

    @GetMapping("/logs/watering")
    @Operation(summary = "급수 로그 조회 API", description = "기간별 급수 로그 조회 (GPT 보고서용)")
    public ApiResponse<List<DispenserResponseDTO.WateringLogDTO>> getWateringLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, dispenserQueryService.getWateringLogs(SecurityUtil.getCurrentMemberId(), from, to));
    }
}
