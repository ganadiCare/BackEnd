package smCapstone.homecam.domain.member.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;
import smCapstone.homecam.domain.member.exception.MemberErrorCode;
import smCapstone.homecam.domain.member.exception.MemberException;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(MemberRequestDTO.SendCodeDTO request) {
        String email = request.email();
        String code = generateCode();

        // Redis에 5분간 저장 (Key: AUTH:이메일, Value: 인증번호)
        redisTemplate.opsForValue().set("AUTH:" + email, code, Duration.ofMinutes(5));

        // 메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("[홈캠] 회원가입 인증번호입니다.");
        message.setText("인증번호 6자리를 입력해주세요: " + code);
        mailSender.send(message);
    }

    @Override
    public void verifyCheckCode(MemberRequestDTO.VerifyCodeDTO request) {
        String savedCode = redisTemplate.opsForValue().get("AUTH:" + request.email());

        // 인증 실패 시 예외 던지기
        if (savedCode == null || !request.code().equals(savedCode)) {
            throw new MemberException(MemberErrorCode.INVALID_CHECK_CODE);
        }

        // 인증 성공 시 Redis 데이터 삭제 (1회용)
        redisTemplate.delete("AUTH:" + request.email());
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}