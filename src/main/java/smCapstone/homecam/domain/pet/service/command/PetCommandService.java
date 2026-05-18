package smCapstone.homecam.domain.pet.service.command;

import smCapstone.homecam.domain.pet.dto.request.PetRequestDTO;
import smCapstone.homecam.domain.pet.dto.response.PetResponseDTO;

public interface PetCommandService {
    PetResponseDTO.PetDTO updatePet(Long memberId, PetRequestDTO.UpdatePetDTO request);
    void deletePet(Long memberId);
}
