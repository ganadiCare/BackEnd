package smCapstone.homecam.domain.device.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.global.entity.BaseEntity;
import smCapstone.homecam.domain.device.exception.DispenserException;
import smCapstone.homecam.domain.device.exception.DispenserErrorCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Dispenser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceCode;

    private String deviceName;

    private Boolean isAutoFeed;

    private Boolean isAutoWater;

    private Integer minWater;

    private Integer maxWater;

    private Boolean isCleaningMode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "dispenser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeedingSchedule> feedingSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "dispenser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeedingLog> feedingLogs = new ArrayList<>();

    @OneToMany(mappedBy = "dispenser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WateringLog> wateringLogs = new ArrayList<>();

    public void update(String deviceName, Boolean isAutoFeed,
                       Boolean isAutoWater, Integer minWater, Integer maxWater, Boolean isCleaningMode) {
        if (deviceName != null) this.deviceName = deviceName;
        if (isAutoFeed != null) this.isAutoFeed = isAutoFeed;
        if (isAutoWater != null) this.isAutoWater = isAutoWater;
        if (isCleaningMode != null) this.isCleaningMode = isCleaningMode;

        // minWater, maxWater 불변식 검증
        int newMin = minWater != null ? minWater : (this.minWater != null ? this.minWater : 0);
        int newMax = maxWater != null ? maxWater : (this.maxWater != null ? this.maxWater : Integer.MAX_VALUE);
        if (newMin > newMax) {
            throw new DispenserException(DispenserErrorCode.INVALID_WATER_RANGE);
        }
        if (minWater != null) this.minWater = minWater;
        if (maxWater != null) this.maxWater = maxWater;
    }
}
