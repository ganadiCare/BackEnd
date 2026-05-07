package smCapstone.homecam.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;
    private final StringRedisTemplate redisTemplate;

    // application.yml의 값들을 주입받아 초기화합니다.
    public JwtUtil(
            @Value("${jwt.token.secretKey}") String secret,
            @Value("${jwt.token.expiration.access}") Long accessExpiration,
            @Value("${jwt.token.expiration.refresh}") Long refreshExpiration,
            StringRedisTemplate redisTemplate
    ) {
        // JJWT 0.12.x 방식의 키 생성
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.refreshExpiration = Duration.ofMillis(refreshExpiration);
        this.redisTemplate = redisTemplate;
    }

    // AccessToken 생성
    public String createAccessToken(Long memberId, String email, String nickname) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(memberId.toString())
                .claim("email", email)
                .claim("nickname", nickname)
                .claim("tokenType", "access")
                .issuedAt(Date.from(now)) // 발급 시간
                .expiration(Date.from(now.plus(accessExpiration))) // 만료 시간
                .signWith(secretKey) // 서명
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(Long memberId) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(memberId.toString())
                .claim("tokenType", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshExpiration)))
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 Claims(페이로드 정보) 추출
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60) // 서버 간 시간 오차 1분 허용
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰에서 Member ID 추출
    public Long getId(String token) {
        try {
            Claims claims = getClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    // Access 토큰 검증
    public boolean isValidAccessToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) return false;
            Claims claims = getClaims(token);
            return "access".equals(claims.get("tokenType"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Refresh 토큰 검증
    public boolean isValidRefreshToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) return false;
            Claims claims = getClaims(token);
            return "refresh".equals(claims.get("tokenType"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Redis에 Refresh Token 저장
    public void storeRefreshToken(Long memberId, String refreshToken) {
        String key = "refresh:token:" + memberId;
        redisTemplate.opsForValue().set(key, refreshToken, refreshExpiration);
    }

    // Redis에서 Refresh Token 삭제 (로그아웃 시 사용)
    public void invalidateRefreshToken(Long memberId) {
        String key = "refresh:token:" + memberId;
        redisTemplate.delete(key);
    }

    // Refresh Token을 HttpOnly 쿠키에 저장 (보안 강화)
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); // 프론트엔드 JS에서 접근 불가 (XSS 방어)
        cookie.setSecure(false);  // 개발 환경(HTTP)에서는 false, 운영(HTTPS)에서는 true로 변경
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshExpiration.toSeconds());
        cookie.setAttribute("SameSite", "Lax"); // CSRF 방어
        response.addCookie(cookie);
    }

    // 쿠키에서 Refresh Token 꺼내기
    public String getRefreshTokenCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                }
            }
        }
        return null;
    }

    // 쿠키에서 Refresh Token 삭제 (로그아웃 시 사용)
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
}