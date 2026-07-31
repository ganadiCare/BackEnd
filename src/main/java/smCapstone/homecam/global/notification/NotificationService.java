package smCapstone.homecam.global.notification;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(30L * 60L * 1_000L);
        emitters.computeIfAbsent(memberId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(memberId, emitter));
        emitter.onTimeout(() -> remove(memberId, emitter));
        emitter.onError(error -> remove(memberId, emitter));
        try {
            emitter.send(SseEmitter.event().name("connected").data(Map.of("connected", true)));
        } catch (IOException e) {
            remove(memberId, emitter);
        }
        return emitter;
    }

    public void send(Long memberId, String type, Map<String, Object> data) {
        CopyOnWriteArrayList<SseEmitter> memberEmitters = emitters.get(memberId);
        if (memberEmitters == null) {
            return;
        }
        for (SseEmitter emitter : memberEmitters) {
            try {
                emitter.send(SseEmitter.event().name(type).data(data));
            } catch (IOException e) {
                remove(memberId, emitter);
            }
        }
    }

    private void remove(Long memberId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> memberEmitters = emitters.get(memberId);
        if (memberEmitters == null) {
            return;
        }
        memberEmitters.remove(emitter);
        if (memberEmitters.isEmpty()) {
            emitters.remove(memberId);
        }
    }
}
