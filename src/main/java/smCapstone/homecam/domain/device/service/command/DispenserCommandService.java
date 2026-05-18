package smCapstone.homecam.domain.device.service.command;

import smCapstone.homecam.domain.device.dto.request.DispenserRequestDTO;
import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;

public interface DispenserCommandService {
    DispenserResponseDTO.DispenserDTO updateDispenser(Long memberId, DispenserRequestDTO.UpdateDispenserDTO request);
    void deleteDispenser(Long memberId);

    DispenserResponseDTO.ScheduleDTO createSchedule(Long memberId, DispenserRequestDTO.CreateScheduleDTO request);
    DispenserResponseDTO.ScheduleDTO updateSchedule(Long memberId, Long scheduleId, DispenserRequestDTO.UpdateScheduleDTO request);
    void deleteSchedule(Long memberId, Long scheduleId);
}
