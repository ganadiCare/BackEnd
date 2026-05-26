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

@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private WebSocketSession piSession = null;
    private final Map<String, WebSocketSession> browsers = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode msg = mapper.readTree(message.getPayload());
        String type = msg.get("type").asText();

        switch (type) {
            case "register":
                if ("pi".equals(msg.get("role").asText())) {
                    piSession = session;
                    System.out.println("Pi 연결됨: " + session.getId());
                } else {
                    String sid = msg.get("sessionId").asText();
                    browsers.put(sid, session);
                    System.out.println("브라우저 등록: " + sid);
                    if (piSession != null && piSession.isOpen()) {
                        ObjectNode notify = mapper.createObjectNode();
                        notify.put("type", "browser_connected");
                        notify.put("sessionId", sid);
                        piSession.sendMessage(new TextMessage(notify.toString()));
                    }
                }
                break;

            case "offer":
                System.out.println("Offer 중계 → Pi");
                if (piSession != null && piSession.isOpen()) {
                    piSession.sendMessage(message);
                }
                break;

            case "answer":
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
        if (session.equals(piSession)) {
            piSession = null;
            System.out.println("Pi 연결 끊김");
        } else {
            browsers.values().remove(session);
            System.out.println("브라우저 연결 끊김: " + session.getId());
        }
    }
}
