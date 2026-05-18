package smCapstone.homecam.domain.pet.service.query;

import smCapstone.homecam.domain.pet.dto.response.PetResponseDTO;

public interface PetQueryService {
    PetResponseDTO.PetDTO getMyPet(Long memberId);
}
