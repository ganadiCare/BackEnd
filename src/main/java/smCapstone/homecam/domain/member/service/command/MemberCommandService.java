package smCapstone.homecam.domain.member.service.command;

import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;

public interface MemberCommandService {

    void sendVerificationEmail(MemberRequestDTO.SendCodeDTO request);

    void verifyCheckCode(MemberRequestDTO.VerifyCodeDTO request);
}
