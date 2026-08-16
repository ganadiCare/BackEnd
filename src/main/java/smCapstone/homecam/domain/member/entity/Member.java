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


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    // 비즈니스 편의 메서드 (비밀번호 암호화 적용 시 사용)
    public void setEncodedPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /*
    연관 관계 아직 엔티티 안 만들어서 이대로 둠

    // 반려동물 (1:1 관계 가정)
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Pet pet;

    // 등록된 기기 목록 (1:N)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Device> deviceList = new ArrayList<>();
    */
}
