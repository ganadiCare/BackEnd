package smCapstone.homecam.global.webrtc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class TurnCredentialService {

    @Value("${webrtc.turn.secret}")
    private String turnSecret;

    @Value("${webrtc.turn.ttl:86400}")
    private long ttl;

    // TURN 서버 URL은 민감정보 아니므로 직접 명시
    private static final List<String> TURN_URLS = List.of(
        "turn:20.189.241.58:3478?transport=udp",
        "turn:20.189.241.58:3478?transport=tcp"
    );

    /**
     * coturn --use-auth-secret 방식 호환 임시 자격증명 생성
     * username = "만료시각:userId"
     * credential = HMAC-SHA1(secret, username) → Base64
     */
    public TurnCredentialResponse generateCredentials(String userId) {
        long expiry = (System.currentTimeMillis() / 1000L) + ttl;
        String username = expiry + ":" + userId;
        String credential = hmacSha1(turnSecret, username);

        List<Map<String, Object>> iceServers = List.of(
            Map.of(
                "urls",       TURN_URLS,
                "username",   username,
                "credential", credential
            ),
            Map.of("urls", List.of("stun:stun.l.google.com:19302"))
        );

        return new TurnCredentialResponse(iceServers, expiry);
    }

    private String hmacSha1(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            return Base64.getEncoder().encodeToString(
                mac.doFinal(data.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            throw new IllegalStateException("TURN 자격증명 생성 실패", e);
        }
    }
}
