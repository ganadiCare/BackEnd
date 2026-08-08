package smCapstone.homecam.domain.activity.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.activity.dto.ActivityResponseDTO;
import smCapstone.homecam.domain.activity.entity.ActivityLog;
import smCapstone.homecam.domain.activity.repository.ActivityLogRepository;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private final ActivityLogRepository activityLogRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void record(JsonNode message) {
        String detectionId = requiredText(message, "detectionId");
        String cameraSessionId = requiredText(message, "cameraSessionId");
        LocalDateTime cameraStartedAt = parseTime(requiredText(message, "cameraStartedAt"));
        LocalDateTime detectedStartedAt = parseTime(requiredText(message, "detectedStartedAt"));
        LocalDateTime lastDetectedAt = parseTime(requiredText(message, "lastDetectedAt"));
        LocalDateTime detectedEndedAt = nullableTime(message, "detectedEndedAt");

        ActivityLog log = activityLogRepository.findByDetectionId(detectionId)
                .orElseGet(() -> {
                    Member member = memberRepository.findFirstByOrderByIdAsc()
                            .orElseThrow(() -> new IllegalStateException("활동 기록을 연결할 회원이 없습니다."));
                    return ActivityLog.builder()
                            .detectionId(detectionId)
                            .cameraSessionId(cameraSessionId)
                            .cameraStartedAt(cameraStartedAt)
                            .detectedStartedAt(detectedStartedAt)
                            .lastDetectedAt(detectedStartedAt)
                            .detectedSeconds(0L)
                            .member(member)
                            .build();
                });

        log.updateDetection(lastDetectedAt, detectedEndedAt);
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponseDTO.ActivityLogDTO> getLogs(
            Long memberId, LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from은 to보다 늦을 수 없습니다.");
        }
        return activityLogRepository.findInPeriod(memberId, from, to).stream()
                .map(this::toDto)
                .toList();
    }

    private ActivityResponseDTO.ActivityLogDTO toDto(ActivityLog log) {
        return new ActivityResponseDTO.ActivityLogDTO(
                log.getId(), log.getCameraSessionId(), log.getCameraStartedAt(),
                log.getDetectedStartedAt(), log.getLastDetectedAt(), log.getDetectedEndedAt(),
                log.getDetectedSeconds(), log.getDetectedEndedAt() == null);
    }

    private String requiredText(JsonNode message, String field) {
        if (!message.hasNonNull(field) || message.get(field).asText().isBlank()) {
            throw new IllegalArgumentException(field + " 값이 필요합니다.");
        }
        return message.get(field).asText();
    }

    private LocalDateTime nullableTime(JsonNode message, String field) {
        return message.hasNonNull(field) ? parseTime(message.get(field).asText()) : null;
    }

    private LocalDateTime parseTime(String value) {
        return OffsetDateTime.parse(value).atZoneSameInstant(SERVICE_ZONE).toLocalDateTime();
    }
}
