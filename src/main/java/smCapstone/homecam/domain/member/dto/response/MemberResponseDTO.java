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

    @Builder
    public record ProfileDTO(
            String nickname,
            String email,
            PetProfileDTO pet,
            DeviceProfileDTO device
    ) {}

    @Builder
    public record PetProfileDTO(
            String name,
            String birthday, // 프론트에서 포맷팅하기 편하도록
            Integer age,
            Double weight,
            String gender    // Enum 대신 "MALE", "FEMALE" 문자열 반환
    ) {}

    @Builder
    public record DeviceProfileDTO(
            String cameraCode,
            Boolean isPrivateMode,
            Boolean isAutoRecordMode, // UI에 있으므로 필드만 추가 나중에 제어로직 구현
            String nightVision
    ) {}
}