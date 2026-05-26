package smCapstone.homecam.global.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicReference<WebSocketSession> piSession = new AtomicReference<>(null);
    private final Map<String, WebSocketSession> browsers = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode msg;
        try {
            msg = mapper.readTree(message.getPayload());
        } catch (Exception e) {
            System.out.println("JSON 파싱 실패: " + e.getMessage());
            return;
        }

        if (msg == null || !msg.has("type")) {
            System.out.println("type 필드 없음, 무시");
            return;
        }

        String type = msg.get("type").asText();

        switch (type) {
            case "register":
                if (!msg.has("role")) { System.out.println("role 필드 없음"); return; }
                if ("pi".equals(msg.get("role").asText())) {
                    piSession.set(session);
                    System.out.println("Pi 연결됨: " + session.getId());
                } else {
                    if (!msg.has("sessionId")) { System.out.println("sessionId 필드 없음"); return; }
                    String sid = msg.get("sessionId").asText();
                    browsers.put(sid, session);
                    System.out.println("브라우저 등록: " + sid);
                    WebSocketSession pi = piSession.get();
                    if (pi != null && pi.isOpen()) {
                        ObjectNode notify = mapper.createObjectNode();
                        notify.put("type", "browser_connected");
                        notify.put("sessionId", sid);
                        pi.sendMessage(new TextMessage(notify.toString()));
                    }
                }
                break;

            case "offer":
                System.out.println("Offer 중계 → Pi");
                WebSocketSession pi = piSession.get();
                if (pi != null && pi.isOpen()) {
                    pi.sendMessage(message);
                }
                break;

            case "answer":
                if (!msg.has("sessionId")) { System.out.println("sessionId 필드 없음"); return; }
                String sid = msg.get("sessionId").asText();
                System.out.println("Answer 중계 → 브라우저: " + sid);
                WebSocketSession browser = browsers.get(sid);
                if (browser != null && browser.isOpen()) {
                    browser.sendMessage(message);
                }
                break;

            case "bye":
                String byeSid = msg.has("sessionId") ? msg.get("sessionId").asText() : "";
                browsers.remove(byeSid);
                System.out.println("bye 수신: " + byeSid);
                break;

            default:
                System.out.println("알 수 없는 메시지 타입: " + type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (session.equals(piSession.get())) {
            piSession.set(null);
            System.out.println("Pi 연결 끊김");
        } else {
            browsers.values().remove(session);
            System.out.println("브라우저 연결 끊김: " + session.getId());
        }
    }
}
