package smCapstone.homecam.global.mqtt;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.repository.DispenserRepository;
import smCapstone.homecam.domain.device.repository.FeedingScheduleRepository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class MqttSchedulePublisher {

    private final MqttGateway mqttGateway;
    private final DispenserRepository dispenserRepository;
    private final FeedingScheduleRepository feedingScheduleRepository;
    private final Set<Long> pending = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean initialSyncDone = new AtomicBoolean(false);

    public void publishAfterCommit(Long dispenserId) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            publish(dispenserId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                pending.add(dispenserId);
                publish(dispenserId);
            }
        });
    }

    @Scheduled(initialDelay = 3_000, fixedDelay = 10_000)
    public void retryPending() {
        if (initialSyncDone.compareAndSet(false, true)) {
            dispenserRepository.findAll().forEach(dispenser -> pending.add(dispenser.getId()));
        }
        pending.forEach(this::publish);
    }

    public boolean publish(Long dispenserId) {
        Dispenser dispenser = dispenserRepository.findById(dispenserId).orElse(null);
        if (dispenser == null) {
            pending.remove(dispenserId);
            return false;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("timezone", "Asia/Seoul");
        payload.put("autoFeed", Boolean.TRUE.equals(dispenser.getIsAutoFeed()));
        payload.put("autoWater", Boolean.TRUE.equals(dispenser.getIsAutoWater()));
        payload.put("minWater", valueOrZero(dispenser.getMinWater()));
        payload.put("maxWater", valueOrZero(dispenser.getMaxWater()));
        payload.put("schedules", feedingScheduleRepository.findAllByDispenserId(dispenserId)
                .stream()
                .map(schedule -> Map.of(
                        "id", schedule.getId(),
                        "time", schedule.getFeedTime(),
                        "targetWeight", schedule.getAmount()
                ))
                .toList());

        boolean published = mqttGateway.publishRetained(
                "homecam/config", payload);
        if (published) {
            pending.remove(dispenserId);
        } else {
            pending.add(dispenserId);
        }
        return published;
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
