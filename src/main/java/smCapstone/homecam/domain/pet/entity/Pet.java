package smCapstone.homecam.domain.pet.entity;

import jakarta.persistence.*;
import lombok.*;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.pet.enums.PetGender;
import smCapstone.homecam.domain.pet.enums.PetSpecies;
import smCapstone.homecam.global.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Pet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING) // 반드시 STRING으로 지정
    private PetSpecies species;

    @Enumerated(EnumType.STRING) // 반드시 STRING으로 지정
    private PetGender gender;

    private Integer age;
    private Double weight;
    private LocalDate birthday;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
