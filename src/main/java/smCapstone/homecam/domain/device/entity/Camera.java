package smCapstone.homecam.domain.device.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.domain.device.enums.NightVision;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.global.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Camera extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceCode;

    private String deviceName;

    @Enumerated(EnumType.STRING)
    private NightVision nightVision;

    private Boolean isPrivateMode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void update(String deviceName, NightVision nightVision, Boolean isPrivateMode) {
        if (deviceName != null) this.deviceName = deviceName;
        if (nightVision != null) this.nightVision = nightVision;
        if (isPrivateMode != null) this.isPrivateMode = isPrivateMode;
    }
}
