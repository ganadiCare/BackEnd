package smCapstone.homecam.domain.member.dto.response;

import lombok.Builder;

public class MemberResponseDTO {

    @Builder
    public record RefreshResultDTO(
            String accessToken
    ) {}

    @Builder
    public record LoginResultDTO(
            Long memberId,
            String accessToken
    ) {}

    @Builder
    public record SignUpResultDTO(
            Long memberId,
            String email,
            String nickname
    ) {}
}