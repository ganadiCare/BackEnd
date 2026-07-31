package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.WateringLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WateringLogRepository extends JpaRepository<WateringLog, Long> {
    boolean existsByMqttEventId(String mqttEventId);
    Optional<WateringLog> findTopByDispenserIdOrderByWateringTimeDesc(Long dispenserId);
    List<WateringLog> findAllByDispenserIdAndWateringTimeBetween(Long dispenserId, LocalDateTime from, LocalDateTime to);
    void deleteAllByDispenserId(Long dispenserId);
}
