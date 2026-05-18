package smCapstone.homecam.domain.device.service.command;

import smCapstone.homecam.domain.device.dto.request.LogRequestDTO;
import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;

public interface LogCommandService {
    DispenserResponseDTO.FeedingLogDTO createFeedingLog(Long memberId, LogRequestDTO.CreateFeedingLogDTO request);
    DispenserResponseDTO.WateringLogDTO createWateringLog(Long memberId, LogRequestDTO.CreateWateringLogDTO request);
}
