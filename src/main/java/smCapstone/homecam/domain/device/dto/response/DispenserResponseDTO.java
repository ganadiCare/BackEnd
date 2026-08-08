package smCapstone.homecam.domain.device.dto.response;

import java.util.List;

public class DispenserResponseDTO {

    public record DispenserDTO(
            Long dispenserId,
            String deviceCode,
            String deviceName,
            FoodDTO food,
            WaterDTO water,
            Boolean isCleaningMode,
            List<ScheduleDTO> feedingSchedules
    ) {}

    public record FoodDTO(
            Boolean isAutoFeed,
            String latestFeedTime,
            Integer leftovers
    ) {}

    public record WaterDTO(
            Boolean isAutoWater,
            Integer minWater,
            Integer maxWater,
            String latestWateringTime,
            Integer leftovers
    ) {}

    public record ScheduleDTO(
            Long scheduleId,
            String feedTime,
            Integer amount
    ) {}

    public record FeedingLogDTO(
            Long logId,
            String feedTime,
            Integer amount,
            Integer leftovers,
            String logType
    ) {}

    public record WateringLogDTO(
            Long logId,
            String wateringTime,
            Integer amount,
            Integer leftovers
    ) {}
}
