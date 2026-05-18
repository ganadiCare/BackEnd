package smCapstone.homecam.domain.member.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.entity.Camera;
import smCapstone.homecam.domain.device.repository.CameraRepository;
import smCapstone.homecam.domain.member.converter.MemberConverter;
import smCapstone.homecam.domain.member.dto.response.MemberResponseDTO;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.member.exception.MemberErrorCode;
import smCapstone.homecam.domain.member.exception.MemberException;
import smCapstone.homecam.domain.member.repository.MemberRepository;
import smCapstone.homecam.domain.pet.entity.Pet;
import smCapstone.homecam.domain.pet.repository.PetRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final CameraRepository cameraRepository;

    @Override
    public MemberResponseDTO.ProfileDTO getMyProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Pet pet = petRepository.findByMemberId(memberId).orElse(null);
        Camera camera = cameraRepository.findByMemberId(memberId).orElse(null);

        return MemberConverter.toProfileDTO(member, pet, camera);
    }
}
