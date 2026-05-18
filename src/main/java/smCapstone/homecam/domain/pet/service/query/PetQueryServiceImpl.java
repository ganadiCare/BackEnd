package smCapstone.homecam.domain.pet.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.pet.converter.PetConverter;
import smCapstone.homecam.domain.pet.dto.response.PetResponseDTO;
import smCapstone.homecam.domain.pet.entity.Pet;
import smCapstone.homecam.domain.pet.exception.PetErrorCode;
import smCapstone.homecam.domain.pet.exception.PetException;
import smCapstone.homecam.domain.pet.repository.PetRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetQueryServiceImpl implements PetQueryService {

    private final PetRepository petRepository;

    @Override
    public List<PetResponseDTO.PetDTO> getMyPets(Long memberId) {
        return petRepository.findAllByMemberId(memberId).stream()
                .map(PetConverter::toPetDTO)
                .toList();
    }

    @Override
    public PetResponseDTO.PetDTO getPet(Long memberId, Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        if (!pet.getMember().getId().equals(memberId)) {
            throw new PetException(PetErrorCode.PET_NOT_OWNED);
        }

        return PetConverter.toPetDTO(pet);
    }
}
