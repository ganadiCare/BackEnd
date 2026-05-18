package smCapstone.homecam.domain.member.service.query;

import smCapstone.homecam.domain.member.dto.response.MemberResponseDTO;

public interface MemberQueryService {
    MemberResponseDTO.ProfileDTO getMyProfile(Long memberId);
}
