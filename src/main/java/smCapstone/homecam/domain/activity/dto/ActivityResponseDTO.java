package smCapstone.homecam.domain.activity.dto;

import java.time.LocalDateTime;

public class ActivityResponseDTO {
    public record ActivityLogDTO(
            Long activityId,
            String cameraSessionId,
            LocalDateTime cameraStartedAt,
            LocalDateTime detectedStartedAt,
            LocalDateTime lastDetectedAt,
            LocalDateTime detectedEndedAt,
            Long detectedSeconds,
            boolean detecting
    ) {}
}
