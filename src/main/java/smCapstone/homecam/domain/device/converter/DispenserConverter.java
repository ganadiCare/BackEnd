package smCapstone.homecam.domain.device.converter;

import smCapstone.homecam.domain.device.dto.response.DispenserResponseDTO;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.entity.FeedingLog;
import smCapstone.homecam.domain.device.entity.FeedingSchedule;
import smCapstone.homecam.domain.device.entity.WateringLog;

import java.time.format.DateTimeFormatter;

public class DispenserConverter {

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static DispenserResponseDTO.ScheduleDTO toScheduleDTO(FeedingSchedule schedule) {
        return new DispenserResponseDTO.ScheduleDTO(
                schedule.getId(),
                schedule.getFeedTime(),
                schedule.getAmount()
        );
    }

    public static DispenserResponseDTO.FeedingLogDTO toFeedingLogDTO(FeedingLog log) {
        return new DispenserResponseDTO.FeedingLogDTO(
                log.getId(),
                log.getFeedTime() != null ? log.getFeedTime().format(DATETIME_FMT) : null,
                log.getAmount(),
                log.getLeftovers()
        );
    }

    public static DispenserResponseDTO.WateringLogDTO toWateringLogDTO(WateringLog log) {
        return new DispenserResponseDTO.WateringLogDTO(
                log.getId(),
                log.getWateringTime() != null ? log.getWateringTime().format(DATETIME_FMT) : null,
                log.getAmount(),
                log.getLeftovers()
        );
    }

    public static DispenserResponseDTO.DispenserDTO toDispenserDTO(
            Dispenser dispenser,
            FeedingLog latestFeedingLog,
            WateringLog latestWateringLog
    ) {
        DispenserResponseDTO.FoodDTO foodDTO = new DispenserResponseDTO.FoodDTO(
                dispenser.getIsAutoFeed(),
                latestFeedingLog != null ? latestFeedingLog.getFeedTime().format(TIME_FMT) : null,
                latestFeedingLog != null ? latestFeedingLog.getLeftovers() : null
        );

        DispenserResponseDTO.WaterDTO waterDTO = new DispenserResponseDTO.WaterDTO(
                dispenser.getIsAutoWater(),
                dispenser.getMinWater(),
                dispenser.getMaxWater(),
                latestWateringLog != null ? latestWateringLog.getWateringTime().format(TIME_FMT) : null,
                latestWateringLog != null ? latestWateringLog.getLeftovers() : null
        );

        return new DispenserResponseDTO.DispenserDTO(
                dispenser.getId(),
                dispenser.getDeviceCode(),
                dispenser.getDeviceName(),
                foodDTO,
                waterDTO,
                dispenser.getIsCleaningMode(),
                dispenser.getFeedingSchedules().stream()
                        .map(DispenserConverter::toScheduleDTO)
                        .toList()
        );
    }
}
