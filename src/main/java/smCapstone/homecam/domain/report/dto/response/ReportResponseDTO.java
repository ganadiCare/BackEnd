package smCapstone.homecam.domain.report.dto.response;

import java.time.LocalDate;
import java.util.List;

public class ReportResponseDTO {

    public record ReportDTO(
            Long reportId,
            LocalDate reportDate,
            String aiSummary,
            String memo,
            FeedingSummaryDTO feeding,
            WateringSummaryDTO watering
    ) {}

    // 그래프용 raw 데이터
    public record FeedingSummaryDTO(
            Integer totalAmount,    // 총 급식량 (g)
            Integer totalCount,     // 급식 횟수
            Integer leftovers,      // 잔여량 (g)
            List<FeedingLogSummary> logs
    ) {}

    public record WateringSummaryDTO(
            Integer totalAmount,    // 총 급수량 (ml)
            Integer totalCount,     // 급수 횟수
            Integer leftovers,      // 잔여량 (ml)
            List<WateringLogSummary> logs
    ) {}

    public record FeedingLogSummary(
            String feedTime,        // "HH:mm"
            Integer amount,
            Integer leftovers
    ) {}

    public record WateringLogSummary(
            String wateringTime,    // "HH:mm"
            Integer amount,
            Integer leftovers
    ) {}

    public record ReportListDTO(
            Long reportId,
            LocalDate reportDate,
            String aiSummary
    ) {}
}
