package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.FeedingSchedule;

import java.util.List;
import java.util.Optional;

public interface FeedingScheduleRepository extends JpaRepository<FeedingSchedule, Long> {
    List<FeedingSchedule> findAllByDispenserId(Long dispenserId);
    // dispenserId + scheduleId 조합 조회 (보안: 존재 여부 노출 방지)
    Optional<FeedingSchedule> findByIdAndDispenserId(Long id, Long dispenserId);
}
