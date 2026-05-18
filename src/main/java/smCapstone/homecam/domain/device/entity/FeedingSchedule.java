package smCapstone.homecam.domain.device.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.global.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FeedingSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feedTime; // "HH:mm"

    private Integer amount;  // 급식량 (g)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispenser_id")
    private Dispenser dispenser;

    public void update(String feedTime, Integer amount) {
        if (feedTime != null) this.feedTime = feedTime;
        if (amount != null) this.amount = amount;
    }
}
