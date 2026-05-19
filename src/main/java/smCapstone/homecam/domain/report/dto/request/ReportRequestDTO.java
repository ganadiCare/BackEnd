package smCapstone.homecam.domain.report.dto.request;

import java.time.LocalDate;

public class ReportRequestDTO {

    public record GenerateReportDTO(
            LocalDate reportDate  // 보고서 생성할 날짜 (null이면 오늘)
    ) {}

    public record UpdateMemoDTO(
            String memo
    ) {}
}
