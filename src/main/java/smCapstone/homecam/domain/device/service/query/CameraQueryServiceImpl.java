package smCapstone.homecam.domain.device.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.converter.CameraConverter;
import smCapstone.homecam.domain.device.dto.response.CameraResponseDTO;
import smCapstone.homecam.domain.device.exception.CameraErrorCode;
import smCapstone.homecam.domain.device.exception.CameraException;
import smCapstone.homecam.domain.device.repository.CameraRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CameraQueryServiceImpl implements CameraQueryService {

    private final CameraRepository cameraRepository;

    @Override
    public CameraResponseDTO.CameraDTO getMyCamera(Long memberId) {
        return cameraRepository.findByMemberId(memberId)
                .map(CameraConverter::toCameraDTO)
                .orElseThrow(() -> new CameraException(CameraErrorCode.CAMERA_NOT_FOUND));
    }
}
