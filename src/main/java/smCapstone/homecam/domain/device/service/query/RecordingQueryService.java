package smCapstone.homecam.domain.device.service.query;

import smCapstone.homecam.domain.device.dto.response.RecordingResponseDTO;
import smCapstone.homecam.domain.device.entity.Recording;

public interface RecordingQueryService {
    RecordingResponseDTO.RecordingListDTO getList(Long memberId);
    Recording getRecordingFile(Long memberId, Long recordingId);
}
