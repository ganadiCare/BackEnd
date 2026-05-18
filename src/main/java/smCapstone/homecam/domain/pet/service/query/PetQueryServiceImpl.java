package smCapstone.homecam.domain.pet.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.pet.converter.PetConverter;
import smCapstone.homecam.domain.pet.dto.response.PetResponseDTO;
import smCapstone.homecam.domain.pet.exception.PetErrorCode;
import smCapstone.homecam.domain.pet.exception.PetException;
import smCapstone.homecam.domain.pet.repository.PetRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetQueryServiceImpl implements PetQueryService {

    private final PetRepository petRepository;

    @Override
    public PetResponseDTO.PetDTO getMyPet(Long memberId) {
        return petRepository.findByMemberId(memberId)
                .map(PetConverter::toPetDTO)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
    }
}
