package smCapstone.homecam.domain.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smCapstone.homecam.domain.activity.entity.ActivityLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Optional<ActivityLog> findByDetectionId(String detectionId);

    @Query("""
            select a from ActivityLog a
            where a.member.id = :memberId
              and a.detectedStartedAt <= :to
              and coalesce(a.detectedEndedAt, a.lastDetectedAt) >= :from
            order by a.detectedStartedAt desc
            """)
    List<ActivityLog> findInPeriod(@Param("memberId") Long memberId,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to);

    void deleteAllByMemberId(Long memberId);
}
