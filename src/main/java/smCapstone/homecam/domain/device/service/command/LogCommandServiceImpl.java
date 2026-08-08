package smCapstone.homecam.domain.device.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.converter.DispenserConverter;
import smCapstone.homecam.domain.device.dto.request.LogRequestDTO;
import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.entity.FeedingLog;
import smCapstone.homecam.domain.device.entity.WateringLog;
import smCapstone.homecam.domain.device.enums.FeedingLogType;
import smCapstone.homecam.domain.device.exception.DispenserErrorCode;
import smCapstone.homecam.domain.device.exception.DispenserException;
import smCapstone.homecam.domain.device.repository.DispenserRepository;
import smCapstone.homecam.domain.device.repository.FeedingLogRepository;
import smCapstone.homecam.domain.device.repository.WateringLogRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LogCommandServiceImpl implements LogCommandService {

    private final DispenserRepository dispenserRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final WateringLogRepository wateringLogRepository;

    @Override
    public DispenserResponseDTO.FeedingLogDTO createFeedingLog(Long memberId, LogRequestDTO.CreateFeedingLogDTO request) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        FeedingLog log = FeedingLog.builder()
                .feedTime(request.feedTime())
                .amount(request.amount())
                .leftovers(request.leftovers())
                .logType(FeedingLogType.FEEDING)
                .dispenser(dispenser)
                .build();

        feedingLogRepository.save(log);
        return DispenserConverter.toFeedingLogDTO(log);
    }

    @Override
    public DispenserResponseDTO.WateringLogDTO createWateringLog(Long memberId, LogRequestDTO.CreateWateringLogDTO request) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        WateringLog log = WateringLog.builder()
                .wateringTime(request.wateringTime())
                .amount(request.amount())
                .leftovers(request.leftovers())
                .dispenser(dispenser)
                .build();

        wateringLogRepository.save(log);
        return DispenserConverter.toWateringLogDTO(log);
    }
}
