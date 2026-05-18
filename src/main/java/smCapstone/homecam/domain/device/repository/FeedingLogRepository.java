package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.FeedingLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedingLogRepository extends JpaRepository<FeedingLog, Long> {
    // 최근 급식 로그 1건
    Optional<FeedingLog> findTopByDispenserIdOrderByFeedTimeDesc(Long dispenserId);
    // 기간별 로그 (GPT 보고서용)
    List<FeedingLog> findAllByDispenserIdAndFeedTimeBetween(Long dispenserId, LocalDateTime from, LocalDateTime to);
}
