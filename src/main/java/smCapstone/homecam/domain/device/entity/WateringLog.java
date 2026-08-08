package smCapstone.homecam.domain.device.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.domain.device.enums.WateringLogType;
import smCapstone.homecam.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WateringLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime wateringTime;

    private Integer amount;

    private Integer leftovers;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private WateringLogType logType;

    @Column(unique = true, length = 64)
    private String mqttEventId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispenser_id", nullable = false)
    private Dispenser dispenser;

    public boolean isActualWatering() {
        if (logType != null) {
            return logType == WateringLogType.WATERING;
        }
        return amount != null && amount > 0;
    }

    public WateringLogType resolvedLogType() {
        if (logType != null) {
            return logType;
        }
        return amount != null && amount > 0
                ? WateringLogType.WATERING
                : WateringLogType.HOURLY_STATUS;
    }
}
