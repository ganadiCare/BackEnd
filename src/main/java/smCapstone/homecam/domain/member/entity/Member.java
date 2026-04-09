package smCapstone.homecam.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.global.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    // 1. ID 필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. 일반 필드 (ERD 기준)
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    // 3. 비즈니스 편의 메서드 (비밀번호 암호화 적용 시 사용)
    public void setEncodedPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /* 4. 연관 관계 (추후 엔티티 생성 시 주석 해제)
       단방향을 기본으로 하나, 양방향 매핑이 필요할 경우를 대비해 틀을 잡아둡니다.

    // 반려동물 (1:1 관계 가정)
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Pet pet;

    // 등록된 기기 목록 (1:N)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Device> deviceList = new ArrayList<>();
    */
}
