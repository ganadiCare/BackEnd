package smCapstone.homecam.domain.member.service.command;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;
import smCapstone.homecam.domain.member.dto.response.MemberResponseDTO;

public interface MemberCommandService {

    void sendVerificationEmail(MemberRequestDTO.SendCodeDTO request);

    void verifyCheckCode(MemberRequestDTO.VerifyCodeDTO request);

    // 회원가입
    MemberResponseDTO.SignUpResultDTO signUp(MemberRequestDTO.SignUpDTO request);

    // 로그인
    @Transactional(readOnly = true)
    MemberResponseDTO.LoginResultDTO login(MemberRequestDTO.LoginDTO request, HttpServletResponse response);
}
