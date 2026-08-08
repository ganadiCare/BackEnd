package smCapstone.homecam.domain.activity.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.global.entity.BaseEntity;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "activity_log", indexes = {
        @Index(name = "idx_activity_member_started", columnList = "member_id, detected_started_at")
})
public class ActivityLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String detectionId;

    @Column(nullable = false, length = 36)
    private String cameraSessionId;

    @Column(nullable = false)
    private LocalDateTime cameraStartedAt;

    @Column(nullable = false)
    private LocalDateTime detectedStartedAt;

    @Column(nullable = false)
    private LocalDateTime lastDetectedAt;

    private LocalDateTime detectedEndedAt;

    @Column(nullable = false)
    private Long detectedSeconds;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void updateDetection(LocalDateTime lastDetectedAt, LocalDateTime detectedEndedAt) {
        if (lastDetectedAt.isAfter(this.lastDetectedAt)) {
            this.lastDetectedAt = lastDetectedAt;
        }
        if (detectedEndedAt != null) {
            this.detectedEndedAt = detectedEndedAt;
        }
        LocalDateTime end = this.detectedEndedAt != null ? this.detectedEndedAt : this.lastDetectedAt;
        this.detectedSeconds = Math.max(0, Duration.between(this.detectedStartedAt, end).getSeconds());
    }
}
