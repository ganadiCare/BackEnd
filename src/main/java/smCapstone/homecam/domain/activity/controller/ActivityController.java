package smCapstone.homecam.domain.activity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import smCapstone.homecam.domain.activity.dto.ActivityResponseDTO;
import smCapstone.homecam.domain.activity.service.ActivityLogService;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activities")
@Tag(name = "Activity API", description = "카메라 강아지 감지 활동 기록 API")
public class ActivityController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @Operation(summary = "활동 기록 기간 조회")
    public ApiResponse<List<ActivityResponseDTO.ActivityLogDTO>> getActivityLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                activityLogService.getLogs(SecurityUtil.getCurrentMemberId(), from, to));
    }
}
