package smCapstone.homecam.domain.device.dto.response;

import lombok.Builder;
import smCapstone.homecam.domain.device.enums.RecordingType;

import java.time.LocalDateTime;
import java.util.List;

public class RecordingResponseDTO {

    @Builder
    public record UploadResultDTO(
            Long id,
            String originalFileName,
            RecordingType type,
            Long fileSize,
            LocalDateTime createdAt
    ) {}

    @Builder
    public record RecordingListDTO(
            List<RecordingItemDTO> recordings
    ) {}

    @Builder
    public record RecordingItemDTO(
            Long id,
            String originalFileName,
            RecordingType type,
            Long fileSize,
            LocalDateTime createdAt
    ) {}
}
