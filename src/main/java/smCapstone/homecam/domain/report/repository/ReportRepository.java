package smCapstone.homecam.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.report.entity.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByMemberIdAndReportDate(Long memberId, LocalDate reportDate);
    List<Report> findAllByMemberIdOrderByReportDateDesc(Long memberId);
}
