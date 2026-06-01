package smCapstone.homecam.domain.device.service.command;

import org.springframework.web.multipart.MultipartFile;
import smCapstone.homecam.domain.device.dto.response.RecordingResponseDTO;
import smCapstone.homecam.domain.device.enums.RecordingType;

public interface RecordingCommandService {
    RecordingResponseDTO.UploadResultDTO upload(Long memberId, MultipartFile file, RecordingType type);
    void delete(Long memberId, Long recordingId);
}
