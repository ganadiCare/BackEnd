package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.FeedingLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedingLogRepository extends JpaRepository<FeedingLog, Long> {
    Optional<FeedingLog> findTopByDispenserIdOrderByFeedTimeDesc(Long dispenserId);
    List<FeedingLog> findAllByDispenserIdAndFeedTimeBetween(Long dispenserId, LocalDateTime from, LocalDateTime to);
    void deleteAllByDispenserId(Long dispenserId);
}
