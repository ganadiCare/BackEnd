package smCapstone.homecam.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;
import smCapstone.homecam.domain.member.dto.response.MemberResponseDTO;
import smCapstone.homecam.domain.member.service.command.MemberCommandService;
import smCapstone.homecam.domain.member.service.query.MemberQueryService;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "Member API", description = "회원 인증 및 관리 API")
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    @PostMapping("/mail/send")
    @Operation(summary = "이메일 인증번호 발송")
    public ApiResponse<String> sendMail(@RequestBody @Valid MemberRequestDTO.SendCodeDTO request) {
        memberCommandService.sendVerificationEmail(request);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, request.email());
    }

    @PostMapping("/mail/verify")
    @Operation(summary = "인증번호 확인")
    public ApiResponse<String> checkCode(@RequestBody @Valid MemberRequestDTO.VerifyCodeDTO request) {
        memberCommandService.verifyCheckCode(request);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, request.email());
    }

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

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "쿠키에 저장된 리프레시 토큰을 무효화합니다.")
    public ApiResponse<String> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        memberCommandService.logout(refreshToken, response);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "성공적으로 로그아웃 되었습니다.");
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 재발급 API", description = "쿠키에 저장된 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    public ApiResponse<MemberResponseDTO.RefreshResultDTO> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        MemberResponseDTO.RefreshResultDTO result = memberCommandService.reissueToken(refreshToken);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @GetMapping("/profile")
    @Operation(summary = "프로필 조회 API", description = "사용자의 계정, 반려동물, 기기 설정 정보를 조회합니다.")
    public ApiResponse<MemberResponseDTO.ProfileDTO> getProfile() {
        MemberResponseDTO.ProfileDTO result = memberQueryService.getMyProfile(SecurityUtil.getCurrentMemberId());
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }
}
