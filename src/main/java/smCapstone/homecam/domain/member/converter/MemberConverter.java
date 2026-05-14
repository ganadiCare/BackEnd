package smCapstone.homecam.domain.member.converter;

import smCapstone.homecam.domain.device.entity.Camera;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.enums.NightVision;
import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.pet.entity.Pet;

public class MemberConverter {

    public static Camera toCamera(String code, Member member) {
        if (code == null || code.isBlank()) return null;
        return Camera.builder()
                .deviceCode(code)
                .nightVision(NightVision.AUTO)
                .isPrivateMode(false)
                .member(member)
                .build();
    }

    public static Dispenser toDispenser(String code, Member member) {
        if (code == null || code.isBlank()) return null;
        return Dispenser.builder()
                .deviceCode(code)
                .member(member)
                .build();
    }

    public static Pet toPet(MemberRequestDTO.PetSettingDTO dto, Member member) {
        if (dto == null) return null;
        return Pet.builder()
                .name(dto.name())
                .species(dto.species())
                .gender(dto.gender())
                .age(dto.age())
                .weight(dto.weight())
                .birthday(dto.birthday())
                .member(member)
                .build();
    }
}