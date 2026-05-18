package smCapstone.homecam.domain.pet.dto.response;

import smCapstone.homecam.domain.pet.enums.PetGender;
import smCapstone.homecam.domain.pet.enums.PetSpecies;

import java.time.LocalDate;

public class PetResponseDTO {

    public record PetDTO(
            Long petId,
            String name,
            PetSpecies species,
            PetGender gender,
            Integer age,
            Double weight,
            LocalDate birthday
    ) {}
}
