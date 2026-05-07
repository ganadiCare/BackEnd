package smCapstone.homecam.domain.member.service.command;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;
import smCapstone.homecam.domain.member.dto.response.MemberResponseDTO;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.member.exception.MemberErrorCode;
import smCapstone.homecam.domain.member.exception.MemberException;
import smCapstone.homecam.domain.member.repository.MemberRepository;
import smCapstone.homecam.global.util.JwtUtil;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화
    private final smCapstone.homecam.global.util.JwtUtil jwtUtil; // 토큰 발급기

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(MemberRequestDTO.SendCodeDTO request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new MemberException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
        }

        String code = generateCode();
        redisTemplate.opsForValue().set("AUTH:" + request.email(), code, Duration.ofMinutes(5));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(request.email());
        message.setSubject("[HomeCam] 회원가입 이메일 인증번호입니다.");
        message.setText("인증번호 : " + code);
        mailSender.send(message);
    }

    @Override
    public void verifyCheckCode(MemberRequestDTO.VerifyCodeDTO request) {
        String savedCode = redisTemplate.opsForValue().get("AUTH:" + request.email());

        if (savedCode == null || !request.code().equals(savedCode)) {
            throw new MemberException(MemberErrorCode.INVALID_CHECK_CODE);
        }

        redisTemplate.delete("AUTH:" + request.email());
        // 인증 성공 시 가입 허가 키를 30분간 Redis에 저장
        redisTemplate.opsForValue().set("VERIFIED:" + request.email(), "TRUE", Duration.ofMinutes(30));
    }

    // 회원가입
    @Override
    public MemberResponseDTO.SignUpResultDTO signUp(MemberRequestDTO.SignUpDTO request) {
        // 이메일 중복 검증
        if (memberRepository.existsByEmail(request.email())) {
            throw new MemberException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // 이메일 인증 여부 검증 (Redis에 VERIFIED 키가 있는지 확인)
        String isVerified = redisTemplate.opsForValue().get("VERIFIED:" + request.email());
        if (isVerified == null) {
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 회원 저장 (비밀번호 암호화)
        Member newMember = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        memberRepository.save(newMember);

        // 가입 완료 후 Redis의 인증 키 삭제 - 재사용 방지 목적
        redisTemplate.delete("VERIFIED:" + request.email());

        return MemberResponseDTO.SignUpResultDTO.builder()
                .memberId(newMember.getId())
                .email(newMember.getEmail())
                .nickname(newMember.getNickname())
                .build();
    }

    // 로그인
    @Transactional(readOnly = true)
    @Override
    public MemberResponseDTO.LoginResultDTO login(MemberRequestDTO.LoginDTO request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        // Access Token, Refresh Token 동시 생성
        String accessToken = jwtUtil.createAccessToken(member.getId(), member.getEmail(), member.getNickname());
        String refreshToken = jwtUtil.createRefreshToken(member.getId());

        // Refresh Token을 Redis에 저장
        jwtUtil.storeRefreshToken(member.getId(), refreshToken);

        // Refresh Token을 응답 쿠키(HttpOnly)에 담기
        jwtUtil.setRefreshTokenCookie(response, refreshToken);

        return MemberResponseDTO.LoginResultDTO.builder()
                .memberId(member.getId())
                .accessToken(accessToken) // Access Token은 JSON 바디로 전달
                .build();
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}