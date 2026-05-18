package smCapstone.homecam.domain.device.service.query;

import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface DispenserQueryService {
    DispenserResponseDTO.DispenserDTO getMyDispenser(Long memberId);
    List<DispenserResponseDTO.FeedingLogDTO> getFeedingLogs(Long memberId, LocalDateTime from, LocalDateTime to);
    List<DispenserResponseDTO.WateringLogDTO> getWateringLogs(Long memberId, LocalDateTime from, LocalDateTime to);
}
