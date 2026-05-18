package smCapstone.homecam.domain.device.dto.response;

import smCapstone.homecam.domain.device.enums.NightVision;

public class CameraResponseDTO {

    public record CameraDTO(
            Long cameraId,
            String deviceCode,
            String deviceName,
            NightVision nightVision,
            Boolean isPrivateMode,
            Boolean isAutoRecordMode
    ) {}
}
