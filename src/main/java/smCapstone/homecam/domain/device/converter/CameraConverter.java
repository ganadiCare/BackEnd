package smCapstone.homecam.domain.device.converter;

import smCapstone.homecam.domain.device.dto.response.CameraResponseDTO;
import smCapstone.homecam.domain.device.entity.Camera;

public class CameraConverter {

    public static CameraResponseDTO.CameraDTO toCameraDTO(Camera camera) {
        return new CameraResponseDTO.CameraDTO(
                camera.getId(),
                camera.getDeviceCode(),
                camera.getDeviceName(),
                camera.getNightVision(),
                camera.getIsPrivateMode()
        );
    }
}
