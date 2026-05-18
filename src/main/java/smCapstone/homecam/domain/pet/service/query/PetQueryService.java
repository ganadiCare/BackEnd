package smCapstone.homecam.domain.pet.service.query;

import smCapstone.homecam.domain.pet.dto.response.PetResponseDTO;

import java.util.List;

public interface PetQueryService {
    List<PetResponseDTO.PetDTO> getMyPets(Long memberId);
    PetResponseDTO.PetDTO getPet(Long memberId, Long petId);
}
