package smCapstone.homecam.domain.report.service;

import smCapstone.homecam.domain.report.dto.request.ReportRequestDTO;
import smCapstone.homecam.domain.report.dto.response.ReportResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    ReportResponseDTO.ReportDTO generateReport(Long memberId, LocalDate reportDate);
    ReportResponseDTO.ReportDTO getReport(Long memberId, LocalDate reportDate);
    List<ReportResponseDTO.ReportListDTO> getReportList(Long memberId);
    ReportResponseDTO.ReportDTO updateMemo(Long memberId, Long reportId, ReportRequestDTO.UpdateMemoDTO request);
    void deleteReport(Long memberId, Long reportId);
}
