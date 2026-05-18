package smCapstone.homecam.domain.pet.converter;

import smCapstone.homecam.domain.pet.dto.response.PetResponseDTO;
import smCapstone.homecam.domain.pet.entity.Pet;

public class PetConverter {

    public static PetResponseDTO.PetDTO toPetDTO(Pet pet) {
        return new PetResponseDTO.PetDTO(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getGender(),
                pet.getAge(),
                pet.getWeight(),
                pet.getBirthday()
        );
    }
}
