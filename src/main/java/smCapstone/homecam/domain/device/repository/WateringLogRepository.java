package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.WateringLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WateringLogRepository extends JpaRepository<WateringLog, Long> {
    // 최근 급수 로그 1건
    Optional<WateringLog> findTopByDispenserIdOrderByWateringTimeDesc(Long dispenserId);
    // 기간별 로그 (GPT 보고서용)
    List<WateringLog> findAllByDispenserIdAndWateringTimeBetween(Long dispenserId, LocalDateTime from, LocalDateTime to);
}
