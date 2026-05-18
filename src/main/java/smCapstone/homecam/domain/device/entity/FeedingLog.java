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
public class FeedingLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime feedTime;  // 급식 시각

    private Integer amount;          // 급식량 (g)

    private Integer leftovers;       // 잔여량 (g)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispenser_id")
    private Dispenser dispenser;
}
