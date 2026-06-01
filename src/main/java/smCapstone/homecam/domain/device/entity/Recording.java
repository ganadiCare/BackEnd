package smCapstone.homecam.domain.device.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.domain.device.enums.RecordingType;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.global.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recording extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;   // 원본 파일명

    private String storedFileName;     // 서버 저장 파일명 (UUID)

    private String filePath;           // 저장 경로

    private Long fileSize;             // 바이트

    @Enumerated(EnumType.STRING)
    private RecordingType type;        // VIDEO, IMAGE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
