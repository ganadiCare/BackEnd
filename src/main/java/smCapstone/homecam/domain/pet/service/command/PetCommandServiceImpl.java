package smCapstone.homecam.domain.pet.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.pet.converter.PetConverter;
import smCapstone.homecam.domain.pet.dto.request.PetRequestDTO;
import smCapstone.homecam.domain.pet.dto.response.PetResponseDTO;
import smCapstone.homecam.domain.pet.entity.Pet;
import smCapstone.homecam.domain.pet.exception.PetErrorCode;
import smCapstone.homecam.domain.pet.exception.PetException;
import smCapstone.homecam.domain.pet.repository.PetRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PetCommandServiceImpl implements PetCommandService {

    private final PetRepository petRepository;

    @Override
    public PetResponseDTO.PetDTO updatePet(Long memberId, PetRequestDTO.UpdatePetDTO request) {
        Pet pet = petRepository.findByMemberId(memberId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        pet.update(request.name(), request.species(), request.gender(),
                request.age(), request.weight(), request.birthday());

        return PetConverter.toPetDTO(pet);
    }

    @Override
    public void deletePet(Long memberId) {
        Pet pet = petRepository.findByMemberId(memberId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        petRepository.delete(pet);
    }
}
