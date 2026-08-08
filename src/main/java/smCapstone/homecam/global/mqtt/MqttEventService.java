package smCapstone.homecam.global.mqtt;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.entity.FeedingLog;
import smCapstone.homecam.domain.device.entity.WateringLog;
import smCapstone.homecam.domain.device.enums.FeedingLogType;
import smCapstone.homecam.domain.device.enums.WateringLogType;
import smCapstone.homecam.domain.device.repository.DispenserRepository;
import smCapstone.homecam.domain.device.repository.FeedingLogRepository;
import smCapstone.homecam.domain.device.repository.WateringLogRepository;
import smCapstone.homecam.global.notification.NotificationService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MqttEventService {

    private static final Logger log = LoggerFactory.getLogger(MqttEventService.class);
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final ObjectMapper objectMapper;
    private final DispenserRepository dispenserRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final WateringLogRepository wateringLogRepository;
    private final NotificationService notificationService;

    @Transactional
    public void handle(String rawPayload) {
        try {
            JsonNode event = objectMapper.readTree(rawPayload);
            String eventId = requiredText(event, "eventId");
            String type = requiredText(event, "type");
            Dispenser dispenser = dispenserRepository.findFirstByOrderByIdAsc()
                    .orElseThrow(() -> new IllegalStateException("No dispenser is registered"));

            int amount = nonNegative(event.path("amount").asInt());
            int leftovers = nonNegative(event.path("after").asInt());
            LocalDateTime now = LocalDateTime.now(KST);

            if ("feeding".equals(type)) {
                if (feedingLogRepository.existsByMqttEventId(eventId)) {
                    return;
                }
                int before = nonNegative(event.path("before").asInt(leftovers));
                feedingLogRepository.save(FeedingLog.builder()
                        .feedTime(now)
                        .amount(amount)
                        .leftovers(leftovers)
                        .consumedAmount(calculateFoodConsumed(dispenser, before))
                        .logType(FeedingLogType.FEEDING)
                        .mqttEventId(eventId)
                        .dispenser(dispenser)
                        .build());
                notify(dispenser, "feeding-completed", amount, leftovers);
            } else if ("food-status".equals(type)) {
                if (feedingLogRepository.existsByMqttEventId(eventId)) {
                    return;
                }
                feedingLogRepository.save(FeedingLog.builder()
                        .feedTime(now)
                        .amount(0)
                        .leftovers(leftovers)
                        .consumedAmount(calculateFoodConsumed(dispenser, leftovers))
                        .logType(FeedingLogType.HOURLY_STATUS)
                        .mqttEventId(eventId)
                        .dispenser(dispenser)
                        .build());
            } else if ("watering".equals(type)) {
                if (wateringLogRepository.existsByMqttEventId(eventId)) {
                    return;
                }
                wateringLogRepository.save(WateringLog.builder()
                        .wateringTime(now)
                        .amount(amount)
                        .leftovers(leftovers)
                        .logType(WateringLogType.WATERING)
                        .mqttEventId(eventId)
                        .dispenser(dispenser)
                        .build());
                notify(dispenser, "watering-completed", amount, leftovers);
            } else if ("water-status".equals(type)) {
                if (wateringLogRepository.existsByMqttEventId(eventId)) {
                    return;
                }
                wateringLogRepository.save(WateringLog.builder()
                        .wateringTime(now)
                        .amount(0)
                        .leftovers(leftovers)
                        .logType(WateringLogType.HOURLY_STATUS)
                        .mqttEventId(eventId)
                        .dispenser(dispenser)
                        .build());
            }
        } catch (Exception e) {
            log.error("Invalid MQTT event: {}", rawPayload, e);
        }
    }

    private void notify(Dispenser dispenser, String type, int amount, int leftovers) {
        if (dispenser.getMember() == null) {
            return;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("amount", amount);
        data.put("leftovers", leftovers);
        data.put("occurredAt", LocalDateTime.now(KST).toString());
        notificationService.send(dispenser.getMember().getId(), type, data);
    }

    private int calculateFoodConsumed(Dispenser dispenser, int currentWeight) {
        return feedingLogRepository.findTopByDispenserIdOrderByFeedTimeDesc(dispenser.getId())
                .map(previous -> Math.max(0,
                        nonNegative(previous.getLeftovers() != null ? previous.getLeftovers() : 0)
                                - nonNegative(currentWeight)))
                .orElse(0);
    }

    private String requiredText(JsonNode node, String field) {
        String value = node.path(field).asText();
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }

    private int nonNegative(int value) {
        return Math.max(value, 0);
    }
}
