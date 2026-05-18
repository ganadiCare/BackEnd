package smCapstone.homecam.domain.device.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.converter.CameraConverter;
import smCapstone.homecam.domain.device.dto.request.CameraRequestDTO;
import smCapstone.homecam.domain.device.dto.response.CameraResponseDTO;
import smCapstone.homecam.domain.device.entity.Camera;
import smCapstone.homecam.domain.device.exception.CameraErrorCode;
import smCapstone.homecam.domain.device.exception.CameraException;
import smCapstone.homecam.domain.device.repository.CameraRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CameraCommandServiceImpl implements CameraCommandService {

    private final CameraRepository cameraRepository;

    @Override
    public CameraResponseDTO.CameraDTO updateCamera(Long memberId, CameraRequestDTO.UpdateCameraDTO request) {
        Camera camera = cameraRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CameraException(CameraErrorCode.CAMERA_NOT_FOUND));

        camera.update(request.deviceName(), request.nightVision(), request.isPrivateMode(), request.isAutoRecordMode());

        return CameraConverter.toCameraDTO(camera);
    }

    @Override
    public void deleteCamera(Long memberId) {
        Camera camera = cameraRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CameraException(CameraErrorCode.CAMERA_NOT_FOUND));

        cameraRepository.delete(camera);
    }
}
