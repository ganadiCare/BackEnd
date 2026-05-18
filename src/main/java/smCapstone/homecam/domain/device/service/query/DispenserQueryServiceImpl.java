package smCapstone.homecam.domain.device.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.converter.DispenserConverter;
import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.entity.FeedingLog;
import smCapstone.homecam.domain.device.entity.WateringLog;
import smCapstone.homecam.domain.device.exception.DispenserErrorCode;
import smCapstone.homecam.domain.device.exception.DispenserException;
import smCapstone.homecam.domain.device.repository.DispenserRepository;
import smCapstone.homecam.domain.device.repository.FeedingLogRepository;
import smCapstone.homecam.domain.device.repository.WateringLogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DispenserQueryServiceImpl implements DispenserQueryService {

    private final DispenserRepository dispenserRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final WateringLogRepository wateringLogRepository;

    @Override
    public DispenserResponseDTO.DispenserDTO getMyDispenser(Long memberId) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        FeedingLog latestFeedingLog = feedingLogRepository
                .findTopByDispenserIdOrderByFeedTimeDesc(dispenser.getId()).orElse(null);

        WateringLog latestWateringLog = wateringLogRepository
                .findTopByDispenserIdOrderByWateringTimeDesc(dispenser.getId()).orElse(null);

        return DispenserConverter.toDispenserDTO(dispenser, latestFeedingLog, latestWateringLog);
    }

    @Override
    public List<DispenserResponseDTO.FeedingLogDTO> getFeedingLogs(Long memberId, LocalDateTime from, LocalDateTime to) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        return feedingLogRepository
                .findAllByDispenserIdAndFeedTimeBetween(dispenser.getId(), from, to)
                .stream()
                .map(DispenserConverter::toFeedingLogDTO)
                .toList();
    }

    @Override
    public List<DispenserResponseDTO.WateringLogDTO> getWateringLogs(Long memberId, LocalDateTime from, LocalDateTime to) {
        Dispenser dispenser = dispenserRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DispenserException(DispenserErrorCode.DISPENSER_NOT_FOUND));

        return wateringLogRepository
                .findAllByDispenserIdAndWateringTimeBetween(dispenser.getId(), from, to)
                .stream()
                .map(DispenserConverter::toWateringLogDTO)
                .toList();
    }
}
