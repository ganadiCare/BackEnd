package smCapstone.homecam.global.webrtc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webrtc")
@Tag(name = "WebRTC API", description = "TURN 서버 임시 자격증명 발급")
public class TurnController {

    private final TurnCredentialService turnCredentialService;

    @GetMapping("/turn-credentials")
    @Operation(
        summary = "TURN 자격증명 발급",
        description = "로그인한 사용자 기준으로 TTL이 있는 임시 TURN 계정을 발급합니다. 하드코딩 대신 이 API를 호출하세요."
    )
    public ApiResponse<TurnCredentialResponse> getTurnCredentials() {
        String userId = SecurityUtil.getCurrentMemberId().toString();
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, turnCredentialService.generateCredentials(userId));
    }
}
