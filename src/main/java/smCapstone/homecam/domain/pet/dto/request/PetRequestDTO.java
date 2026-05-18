package smCapstone.homecam.domain.pet.dto.request;

import jakarta.validation.constraints.*;
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

            @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
            @Max(value = 100, message = "나이는 100 이하여야 합니다.")
            Integer age,

            @DecimalMin(value = "0.0", inclusive = false, message = "몸무게는 0보다 커야 합니다.")
            @DecimalMax(value = "500.0", message = "몸무게는 500kg 이하여야 합니다.")
            Double weight,

            @PastOrPresent(message = "생일은 오늘 이전이어야 합니다.")
            LocalDate birthday
    ) {}

    public record UpdatePetDTO(
            String name,

            PetSpecies species,

            PetGender gender,

            @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
            @Max(value = 100, message = "나이는 100 이하여야 합니다.")
            Integer age,

            @DecimalMin(value = "0.0", inclusive = false, message = "몸무게는 0보다 커야 합니다.")
            @DecimalMax(value = "500.0", message = "몸무게는 500kg 이하여야 합니다.")
            Double weight,

            @PastOrPresent(message = "생일은 오늘 이전이어야 합니다.")
            LocalDate birthday
    ) {}
}
