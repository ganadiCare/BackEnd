package smCapstone.homecam.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.global.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reportDate; // 보고서 날짜 (하루 1회)

    @Column(columnDefinition = "TEXT")
    private String aiSummary;    // GPT 생성 요약

    @Column(columnDefinition = "TEXT")
    private String memo;         // 사용자 메모

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}
