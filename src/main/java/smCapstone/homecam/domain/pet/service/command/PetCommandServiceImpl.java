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
    public PetResponseDTO.PetDTO updatePet(Long memberId, Long petId, PetRequestDTO.UpdatePetDTO request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        if (!pet.getMember().getId().equals(memberId)) {
            throw new PetException(PetErrorCode.PET_NOT_OWNED);
        }

        if (request.name() != null) pet.setName(request.name());
        if (request.species() != null) pet.setSpecies(request.species());
        if (request.gender() != null) pet.setGender(request.gender());
        if (request.age() != null) pet.setAge(request.age());
        if (request.weight() != null) pet.setWeight(request.weight());
        if (request.birthday() != null) pet.setBirthday(request.birthday());

        return PetConverter.toPetDTO(pet);
    }

    @Override
    public void deletePet(Long memberId, Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        if (!pet.getMember().getId().equals(memberId)) {
            throw new PetException(PetErrorCode.PET_NOT_OWNED);
        }

        petRepository.delete(pet);
    }
}
