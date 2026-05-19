package smCapstone.homecam.domain.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import smCapstone.homecam.domain.report.dto.request.ReportRequestDTO;
import smCapstone.homecam.domain.report.dto.response.ReportResponseDTO;
import smCapstone.homecam.domain.report.service.ReportService;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.util.SecurityUtil;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
@Tag(name = "Report API", description = "AI 활동보고서 API")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "보고서 생성 API", description = "지정 날짜의 급식/급수 로그를 GPT로 분석해 보고서를 생성합니다. 날짜 미입력 시 오늘 날짜로 생성됩니다.")
    public ApiResponse<ReportResponseDTO.ReportDTO> generateReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED,
                reportService.generateReport(SecurityUtil.getCurrentMemberId(), reportDate));
    }

    @GetMapping
    @Operation(summary = "보고서 조회 API", description = "날짜별 보고서 조회. 날짜 미입력 시 오늘 날짜 조회")
    public ApiResponse<ReportResponseDTO.ReportDTO> getReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                reportService.getReport(SecurityUtil.getCurrentMemberId(), reportDate));
    }

    @GetMapping("/list")
    @Operation(summary = "보고서 목록 조회 API", description = "달력 뷰용 보고서 목록 (최신순)")
    public ApiResponse<List<ReportResponseDTO.ReportListDTO>> getReportList() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                reportService.getReportList(SecurityUtil.getCurrentMemberId()));
    }

    @PatchMapping("/{reportId}/memo")
    @Operation(summary = "메모 수정 API")
    public ApiResponse<ReportResponseDTO.ReportDTO> updateMemo(
            @PathVariable Long reportId,
            @RequestBody @Valid ReportRequestDTO.UpdateMemoDTO request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                reportService.updateMemo(SecurityUtil.getCurrentMemberId(), reportId, request));
    }
}
