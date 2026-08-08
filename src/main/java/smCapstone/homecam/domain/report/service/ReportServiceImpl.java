package smCapstone.homecam.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.entity.FeedingLog;
import smCapstone.homecam.domain.device.entity.WateringLog;
import smCapstone.homecam.domain.device.repository.DispenserRepository;
import smCapstone.homecam.domain.device.repository.FeedingLogRepository;
import smCapstone.homecam.domain.device.repository.WateringLogRepository;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.member.exception.MemberErrorCode;
import smCapstone.homecam.domain.member.exception.MemberException;
import smCapstone.homecam.domain.member.repository.MemberRepository;
import smCapstone.homecam.domain.pet.entity.Pet;
import smCapstone.homecam.domain.pet.repository.PetRepository;
import smCapstone.homecam.domain.report.dto.request.ReportRequestDTO;
import smCapstone.homecam.domain.report.dto.response.ReportResponseDTO;
import smCapstone.homecam.domain.report.entity.Report;
import smCapstone.homecam.domain.report.exception.ReportErrorCode;
import smCapstone.homecam.domain.report.exception.ReportException;
import smCapstone.homecam.domain.report.repository.ReportRepository;
import smCapstone.homecam.global.gpt.GptClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final DispenserRepository dispenserRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final WateringLogRepository wateringLogRepository;
    private final GptClient gptClient;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public ReportResponseDTO.ReportDTO generateReport(Long memberId, LocalDate reportDate) {
        if (reportDate == null) reportDate = LocalDate.now();

        Pet pet = petRepository.findByMemberId(memberId).orElse(null);
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId).orElse(null);

        LocalDateTime from = reportDate.atStartOfDay();
        LocalDateTime to = reportDate.atTime(LocalTime.MAX);

        List<FeedingLog> feedingLogs = dispenser != null
                ? feedingLogRepository.findActualFeedingsInPeriod(dispenser.getId(), from, to)
                : List.of();
        List<WateringLog> wateringLogs = dispenser != null
                ? wateringLogRepository.findActualWateringsInPeriod(dispenser.getId(), from, to)
                : List.of();

        String prompt = buildPrompt(pet, feedingLogs, wateringLogs, reportDate);
        String aiSummary = gptClient.generateSummary(prompt);

        return saveReport(memberId, reportDate, aiSummary, feedingLogs, wateringLogs);
    }

    @Transactional
    public ReportResponseDTO.ReportDTO saveReport(Long memberId, LocalDate reportDate, String aiSummary,
                                                   List<FeedingLog> feedingLogs, List<WateringLog> wateringLogs) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Report report = reportRepository.findByMemberIdAndReportDate(memberId, reportDate)
                .map(existingReport -> {
                    existingReport.replaceGeneratedContent(aiSummary);
                    return existingReport;
                })
                .orElseGet(() -> Report.builder()
                        .reportDate(reportDate)
                        .aiSummary(aiSummary)
                        .member(member)
                        .build());

        reportRepository.save(report);
        return toReportDTO(report, feedingLogs, wateringLogs);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponseDTO.ReportDTO getReport(Long memberId, LocalDate reportDate) {
        if (reportDate == null) reportDate = LocalDate.now();

        Report report = reportRepository.findByMemberIdAndReportDate(memberId, reportDate)
                .orElseThrow(() -> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));

        Dispenser dispenser = dispenserRepository.findByMemberId(memberId).orElse(null);
        LocalDateTime from = reportDate.atStartOfDay();
        LocalDateTime to = reportDate.atTime(LocalTime.MAX);

        List<FeedingLog> feedingLogs = dispenser != null
                ? feedingLogRepository.findActualFeedingsInPeriod(dispenser.getId(), from, to)
                : List.of();
        List<WateringLog> wateringLogs = dispenser != null
                ? wateringLogRepository.findActualWateringsInPeriod(dispenser.getId(), from, to)
                : List.of();

        return toReportDTO(report, feedingLogs, wateringLogs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponseDTO.ReportListDTO> getReportList(Long memberId) {
        return reportRepository.findAllByMemberIdOrderByReportDateDesc(memberId)
                .stream()
                .map(r -> new ReportResponseDTO.ReportListDTO(r.getId(), r.getReportDate(), r.getAiSummary()))
                .toList();
    }

    @Override
    @Transactional
    public ReportResponseDTO.ReportDTO updateMemo(Long memberId, Long reportId, ReportRequestDTO.UpdateMemoDTO request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));

        if (!report.getMember().getId().equals(memberId)) {
            throw new ReportException(ReportErrorCode.REPORT_NOT_FOUND);
        }

        report.updateMemo(request.memo());

        Dispenser dispenser = dispenserRepository.findByMemberId(memberId).orElse(null);
        LocalDateTime from = report.getReportDate().atStartOfDay();
        LocalDateTime to = report.getReportDate().atTime(LocalTime.MAX);

        List<FeedingLog> feedingLogs = dispenser != null
                ? feedingLogRepository.findActualFeedingsInPeriod(dispenser.getId(), from, to)
                : List.of();
        List<WateringLog> wateringLogs = dispenser != null
                ? wateringLogRepository.findActualWateringsInPeriod(dispenser.getId(), from, to)
                : List.of();

        return toReportDTO(report, feedingLogs, wateringLogs);
    }

    @Override
    @Transactional
    public void deleteReport(Long memberId, Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));

        if (!report.getMember().getId().equals(memberId)) {
            throw new ReportException(ReportErrorCode.REPORT_NOT_FOUND);
        }

        reportRepository.delete(report);
    }

    private String buildPrompt(Pet pet, List<FeedingLog> feedingLogs, List<WateringLog> wateringLogs, LocalDate date) {
        StringBuilder sb = new StringBuilder();
        sb.append("날짜: ").append(date).append("\n\n");

        if (pet != null) {
            sb.append("반려동물 정보:\n");
            sb.append("- 이름: ").append(pet.getName()).append("\n");
            sb.append("- 종: ").append(pet.getSpecies()).append("\n");
            sb.append("- 나이: ").append(pet.getAge()).append("살\n");
            sb.append("- 몸무게: ").append(pet.getWeight()).append("kg\n\n");
        }

        sb.append("급식 기록:\n");
        if (feedingLogs.isEmpty()) {
            sb.append("- 급식 기록 없음\n");
        } else {
            feedingLogs.forEach(log -> sb.append("- ")
                    .append(log.getFeedTime() != null ? log.getFeedTime().format(TIME_FMT) : "시간 미상")
                    .append(" / 급식량: ").append(log.getAmount()).append("g")
                    .append(" / 잔여량: ").append(log.getLeftovers()).append("g\n"));
            int totalFeed = feedingLogs.stream().mapToInt(l -> l.getAmount() != null ? l.getAmount() : 0).sum();
            sb.append("- 총 급식량: ").append(totalFeed).append("g\n");
        }

        sb.append("\n급수 기록:\n");
        if (wateringLogs.isEmpty()) {
            sb.append("- 급수 기록 없음\n");
        } else {
            wateringLogs.forEach(log -> sb.append("- ")
                    .append(log.getWateringTime() != null ? log.getWateringTime().format(TIME_FMT) : "시간 미상")
                    .append(" / 급수량: ").append(log.getAmount()).append("ml")
                    .append(" / 잔여량: ").append(log.getLeftovers()).append("ml\n"));
            int totalWater = wateringLogs.stream().mapToInt(l -> l.getAmount() != null ? l.getAmount() : 0).sum();
            sb.append("- 총 급수량: ").append(totalWater).append("ml\n");
        }

        sb.append("\n위 데이터를 바탕으로 반려동물의 하루 건강 보고서를 작성해주세요.");
        return sb.toString();
    }

    private ReportResponseDTO.ReportDTO toReportDTO(Report report, List<FeedingLog> feedingLogs, List<WateringLog> wateringLogs) {
        int totalFeedAmount = feedingLogs.stream().mapToInt(l -> l.getAmount() != null ? l.getAmount() : 0).sum();

        int totalFeedLeftovers = feedingLogs.stream()
                .filter(l -> l.getFeedTime() != null)
                .max(Comparator.comparing(FeedingLog::getFeedTime))
                .map(l -> l.getLeftovers() != null ? l.getLeftovers() : 0)
                .orElse(0);

        int totalWaterAmount = wateringLogs.stream().mapToInt(l -> l.getAmount() != null ? l.getAmount() : 0).sum();
        int totalWaterLeftovers = wateringLogs.stream()
                .filter(l -> l.getWateringTime() != null)
                .max(Comparator.comparing(WateringLog::getWateringTime))
                .map(l -> l.getLeftovers() != null ? l.getLeftovers() : 0)
                .orElse(0);

        ReportResponseDTO.FeedingSummaryDTO feedingSummary = new ReportResponseDTO.FeedingSummaryDTO(
                totalFeedAmount,
                feedingLogs.size(),
                totalFeedLeftovers,
                feedingLogs.stream().map(l -> new ReportResponseDTO.FeedingLogSummary(
                        l.getFeedTime() != null ? l.getFeedTime().format(TIME_FMT) : null,
                        l.getAmount(),
                        l.getLeftovers()
                )).toList()
        );

        ReportResponseDTO.WateringSummaryDTO wateringSummary = new ReportResponseDTO.WateringSummaryDTO(
                totalWaterAmount,
                wateringLogs.size(),
                totalWaterLeftovers,
                wateringLogs.stream().map(l -> new ReportResponseDTO.WateringLogSummary(
                        l.getWateringTime() != null ? l.getWateringTime().format(TIME_FMT) : null,
                        l.getAmount(),
                        l.getLeftovers()
                )).toList()
        );

        return new ReportResponseDTO.ReportDTO(
                report.getId(),
                report.getReportDate(),
                report.getAiSummary(),
                report.getMemo(),
                feedingSummary,
                wateringSummary
        );
    }
}
