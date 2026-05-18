package smCapstone.homecam.domain.device.service.query;

import smCapstone.homecam.domain.device.dto.response.CameraResponseDTO;

public interface CameraQueryService {
    CameraResponseDTO.CameraDTO getMyCamera(Long memberId);
}
