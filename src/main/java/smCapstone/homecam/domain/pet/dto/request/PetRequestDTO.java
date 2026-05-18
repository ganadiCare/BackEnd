package smCapstone.homecam.domain.pet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import smCapstone.homecam.domain.pet.enums.PetGender;
import smCapstone.homecam.domain.pet.enums.PetSpecies;

import java.time.LocalDate;

public class PetRequestDTO {

    public record CreatePetDTO(
            @NotBlank(message = "펫 이름은 필수입니다.")
            String name,

            @NotNull(message = "종은 필수입니다.")
            PetSpecies species,

            @NotNull(message = "성별은 필수입니다.")
            PetGender gender,

            Integer age,

            Double weight,

            LocalDate birthday
    ) {}

    public record UpdatePetDTO(
            String name,
            PetSpecies species,
            PetGender gender,
            Integer age,
            Double weight,
            LocalDate birthday
    ) {}
}
