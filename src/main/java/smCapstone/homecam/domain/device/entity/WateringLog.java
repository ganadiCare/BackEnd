package smCapstone.homecam.domain.device.entity;

import jakarta.persistence.*;
import lombok.*;
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

    private LocalDateTime wateringTime; // 급수 시각

    private Integer amount;             // 급수량 (ml)

    private Integer leftovers;          // 잔여량 (ml)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispenser_id")
    private Dispenser dispenser;
}
