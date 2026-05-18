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
            String species,
            String birthday,
            Integer age,
            Double weight,
            String gender
    ) {}

    @Builder
    public record DeviceProfileDTO(
            String cameraCode,
            Boolean isPrivateMode,
            Boolean isAutoRecordMode,
            String nightVision
    ) {}
}
