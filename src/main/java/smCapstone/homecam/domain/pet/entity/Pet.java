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

    @Enumerated(EnumType.STRING)
    private PetSpecies species;

    @Enumerated(EnumType.STRING)
    private PetGender gender;

    private Integer age;

    private Double weight;

    private LocalDate birthday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void update(String name, PetSpecies species, PetGender gender,
                       Integer age, Double weight, LocalDate birthday) {
        if (name != null) this.name = name;
        if (species != null) this.species = species;
        if (gender != null) this.gender = gender;
        if (age != null) this.age = age;
        if (weight != null) this.weight = weight;
        if (birthday != null) this.birthday = birthday;
    }
}
