package smCapstone.homecam.domain.device.service.command;

import smCapstone.homecam.domain.device.dto.request.CameraRequestDTO;
import smCapstone.homecam.domain.device.dto.response.CameraResponseDTO;

public interface CameraCommandService {
    CameraResponseDTO.CameraDTO updateCamera(Long memberId, CameraRequestDTO.UpdateCameraDTO request);
    void deleteCamera(Long memberId);
}
