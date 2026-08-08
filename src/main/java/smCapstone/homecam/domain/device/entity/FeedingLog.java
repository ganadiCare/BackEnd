package smCapstone.homecam.domain.device.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.domain.device.enums.FeedingLogType;
import smCapstone.homecam.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FeedingLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime feedTime;

    private Integer amount;

    private Integer leftovers;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FeedingLogType logType;

    @Column(unique = true, length = 64)
    private String mqttEventId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispenser_id", nullable = false)
    private Dispenser dispenser;

    public boolean isActualFeeding() {
        return logType == null || logType == FeedingLogType.FEEDING;
    }
}
