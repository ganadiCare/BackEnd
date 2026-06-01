package smCapstone.homecam.global.webrtc;

import java.util.List;
import java.util.Map;

public record TurnCredentialResponse(
        List<Map<String, Object>> iceServers,
        long expiresAt
) {}
