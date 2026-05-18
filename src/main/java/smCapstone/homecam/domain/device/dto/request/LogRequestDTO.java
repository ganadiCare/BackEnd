package smCapstone.homecam.domain.device.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class LogRequestDTO {

    public record CreateFeedingLogDTO(
            @NotNull(message = "급식 시각은 필수입니다.")
            LocalDateTime feedTime,
            @NotNull(message = "급식량은 필수입니다.")
            @Min(value = 1, message = "급식량은 1g 이상이어야 합니다.")
            Integer amount,
            @NotNull(message = "잔여량은 필수입니다.")
            @Min(value = 0, message = "잔여량은 0 이상이어야 합니다.")
            Integer leftovers
    ) {}

    public record CreateWateringLogDTO(
            @NotNull(message = "급수 시각은 필수입니다.")
            LocalDateTime wateringTime,
            @NotNull(message = "급수량은 필수입니다.")
            @Min(value = 1, message = "급수량은 1ml 이상이어야 합니다.")
            Integer amount,
            @NotNull(message = "잔여량은 필수입니다.")
            @Min(value = 0, message = "잔여량은 0 이상이어야 합니다.")
            Integer leftovers
    ) {}
}
