package smCapstone.homecam.domain.device.dto.request;

import smCapstone.homecam.domain.device.enums.NightVision;

public class CameraRequestDTO {

    public record UpdateCameraDTO(
            String deviceName,
            NightVision nightVision,
            Boolean isPrivateMode,
            Boolean isAutoRecordMode
    ) {}
}
