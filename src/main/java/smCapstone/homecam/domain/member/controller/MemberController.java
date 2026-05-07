package smCapstone.homecam.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;
import smCapstone.homecam.domain.member.dto.response.MemberResponseDTO;
import smCapstone.homecam.domain.member.service.command.MemberCommandService;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "Member API", description = "회원 인증 및 관리 API")
public class MemberController {

    private final MemberCommandService memberCommandService;

    // 메일 인증번호 발송
    @PostMapping("/mail/send")
    @Operation(summary = "이메일 인증번호 발송")
    public ApiResponse<String> sendMail(@RequestBody @Valid MemberRequestDTO.SendCodeDTO request) {
        memberCommandService.sendVerificationEmail(request);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, request.email());
    }

    // 메일 인증번호 확인
    @PostMapping("/mail/verify")
    @Operation(summary = "인증번호 확인")
    public ApiResponse<String> checkCode(@RequestBody @Valid MemberRequestDTO.VerifyCodeDTO request) {
        memberCommandService.verifyCheckCode(request);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, request.email());
    }

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입 API")
    public ApiResponse<MemberResponseDTO.SignUpResultDTO> signUp(@RequestBody @Valid MemberRequestDTO.SignUpDTO request) {
        MemberResponseDTO.SignUpResultDTO result = memberCommandService.signUp(request);
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, result);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> login(
            @RequestBody @Valid MemberRequestDTO.LoginDTO request,
            HttpServletResponse response
    ) {
        MemberResponseDTO.LoginResultDTO result = memberCommandService.login(request, response);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

}
