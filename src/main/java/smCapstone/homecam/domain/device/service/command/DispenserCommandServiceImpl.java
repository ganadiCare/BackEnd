package smCapstone.homecam.domain.device.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.converter.DispenserConverter;
import smCapstone.homecam.domain.device.dto.request.DispenserRequestDTO;
import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.entity.FeedingLog;
import smCapstone.homecam.domain.device.entity.FeedingSchedule;
import smCapstone.homecam.domain.device.entity.WateringLog;
import smCapstone.homecam.domain.device.exception.DispenserErrorCode;
import smCapstone.homecam.domain.device.exception.DispenserException;
import smCapstone.homecam.domain.device.repository.DispenserRepository;
import smCapstone.homecam.domain.device.repository.FeedingLogRepository;
import smCapstone.homecam.domain.device.repository.FeedingScheduleRepository;
import smCapstone.homecam.domain.device.repository.WateringLogRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DispenserCommandServiceImpl implements DispenserCommandService {

    private final DispenserRepository dispenserRepository;
    private final FeedingScheduleRepository feedingScheduleRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final WateringLogRepository wateringLogRepository;

    @Override
    public DispenserResponseDTO.DispenserDTO updateDispenser(Long memberId, DispenserRequestDTO.UpdateDispenserDTO request) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        dispenser.update(request.deviceName(), request.isAutoFeed(),
                request.isAutoWater(), request.minWater(), request.maxWater(), request.isCleaningMode());

        FeedingLog latestFeedingLog = feedingLogRepository
                .findTopByDispenserIdOrderByFeedTimeDesc(dispenser.getId()).orElse(null);
        WateringLog latestWateringLog = wateringLogRepository
                .findTopByDispenserIdOrderByWateringTimeDesc(dispenser.getId()).orElse(null);

        return DispenserConverter.toDispenserDTO(dispenser, latestFeedingLog, latestWateringLog);
    }

    @Override
    public void deleteDispenser(Long memberId) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));
        dispenserRepository.delete(dispenser);
    }

    @Override
    public DispenserResponseDTO.ScheduleDTO createSchedule(Long memberId, DispenserRequestDTO.CreateScheduleDTO request) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        FeedingSchedule schedule = FeedingSchedule.builder()
                .feedTime(request.feedTime())
                .amount(request.amount())
                .dispenser(dispenser)
                .build();

        feedingScheduleRepository.save(schedule);
        return DispenserConverter.toScheduleDTO(schedule);
    }

    @Override
    public DispenserResponseDTO.ScheduleDTO updateSchedule(Long memberId, Long scheduleId, DispenserRequestDTO.UpdateScheduleDTO request) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        // dispenserId + scheduleId 조합 조회로 존재 여부 노출 방지
        FeedingSchedule schedule = feedingScheduleRepository
                .findByIdAndDispenserId(scheduleId, dispenser.getId())
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.SCHEDULE_NOT_FOUND));

        schedule.update(request.feedTime(), request.amount());
        return DispenserConverter.toScheduleDTO(schedule);
    }

    @Override
    public void deleteSchedule(Long memberId, Long scheduleId) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        // dispenserId + scheduleId 조합 조회로 존재 여부 노출 방지
        FeedingSchedule schedule = feedingScheduleRepository
                .findByIdAndDispenserId(scheduleId, dispenser.getId())
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.SCHEDULE_NOT_FOUND));

        feedingScheduleRepository.delete(schedule);
    }
}
