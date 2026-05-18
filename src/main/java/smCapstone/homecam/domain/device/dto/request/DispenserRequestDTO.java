package smCapstone.homecam.domain.device.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class DispenserRequestDTO {

    public record UpdateDispenserDTO(
            String deviceName,
            Boolean isAutoFeed,
            Boolean isAutoWater,
            @Min(value = 0, message = "최소 수위는 0 이상이어야 합니다.")
            Integer minWater,
            @Min(value = 0, message = "최대 수위는 0 이상이어야 합니다.")
            Integer maxWater,
            Boolean isCleaningMode
    ) {}

    public record CreateScheduleDTO(
            @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "시간 형식은 HH:mm 이어야 합니다.")
            String feedTime,
            @Min(value = 1, message = "급식량은 1g 이상이어야 합니다.")
            Integer amount
    ) {}

    public record UpdateScheduleDTO(
            @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "시간 형식은 HH:mm 이어야 합니다.")
            String feedTime,
            @Min(value = 1, message = "급식량은 1g 이상이어야 합니다.")
            Integer amount
    ) {}
}
