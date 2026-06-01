package smCapstone.homecam.global.webrtc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class TurnCredentialService {

    @Value("${webrtc.turn.secret}")
    private String turnSecret;

    @Value("${webrtc.turn.ttl:86400}")
    private long ttl;

    @Value("${webrtc.turn.urls}")
    private List<String> turnUrls;

    /**
     * coturn --use-auth-secret 방식 호환 임시 자격증명 생성
     * username = "만료시각:userId"
     * credential = HMAC-SHA1(secret, username) → Base64
     */
    public Map<String, Object> generateCredentials(String userId) {
        long expiry = (System.currentTimeMillis() / 1000L) + ttl;
        String username = expiry + ":" + userId;
        String credential = hmacSha1(turnSecret, username);

        return Map.of(
            "iceServers", List.of(
                Map.of(
                    "urls",       turnUrls,
                    "username",   username,
                    "credential", credential
                ),
                Map.of("urls", List.of("stun:stun.l.google.com:19302"))
            ),
            "expiresAt", expiry
        );
    }

    private String hmacSha1(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("TURN 자격증명 생성 실패", e);
        }
    }
}
