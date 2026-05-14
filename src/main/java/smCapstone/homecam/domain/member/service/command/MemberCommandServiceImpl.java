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
import smCapstone.homecam.domain.device.entity.Camera;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.repository.CameraRepository;
import smCapstone.homecam.domain.device.repository.DispenserRepository;
import smCapstone.homecam.domain.member.converter.MemberConverter;
import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;
import smCapstone.homecam.domain.member.dto.response.MemberResponseDTO;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.member.exception.MemberErrorCode;
import smCapstone.homecam.domain.member.exception.MemberException;
import smCapstone.homecam.domain.member.repository.MemberRepository;
import smCapstone.homecam.domain.pet.entity.Pet;
import smCapstone.homecam.domain.pet.repository.PetRepository;
import smCapstone.homecam.global.util.JwtUtil;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final CameraRepository cameraRepository;
    private final DispenserRepository dispenserRepository;
    private final PetRepository petRepository;

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
        redisTemplate.opsForValue().set("VERIFIED:" + request.email(), "TRUE", Duration.ofMinutes(30));
    }

    @Override
    public MemberResponseDTO.SignUpResultDTO signUp(MemberRequestDTO.SignUpDTO request) {
        // 이메일 중복 및 인증 여부 검증
        if (memberRepository.existsByEmail(request.email())) {
            throw new MemberException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
        }
        String isVerified = redisTemplate.opsForValue().get("VERIFIED:" + request.email());
        if (isVerified == null) {
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }

        // Member 저장
        Member newMember = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();
        memberRepository.save(newMember);

        // Camera, Dispenser 초기 설정 저장
        if (request.device() != null) {
            Camera camera = MemberConverter.toCamera(request.device().cameraCode(), newMember);
            if (camera != null) cameraRepository.save(camera);

            Dispenser dispenser = MemberConverter.toDispenser(request.device().dispenserCode(), newMember);
            if (dispenser != null) dispenserRepository.save(dispenser);
        }

        // Pet 초기 설정 저장
        if (request.pet() != null) {
            Pet pet = MemberConverter.toPet(request.pet(), newMember);
            if (pet != null) petRepository.save(pet);
        }

        // 가입 완료 후 인증 키 삭제
        redisTemplate.delete("VERIFIED:" + request.email());

        return MemberResponseDTO.SignUpResultDTO.builder()
                .memberId(newMember.getId())
                .email(newMember.getEmail())
                .nickname(newMember.getNickname())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.LoginResultDTO login(MemberRequestDTO.LoginDTO request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(member.getId(), member.getEmail(), member.getNickname());
        String refreshToken = jwtUtil.createRefreshToken(member.getId());

        jwtUtil.storeRefreshToken(member.getId(), refreshToken);
        jwtUtil.setRefreshTokenCookie(response, refreshToken);

        return MemberResponseDTO.LoginResultDTO.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .build();
    }

    @Override
    public void logout(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && jwtUtil.isValidRefreshToken(refreshToken)) {
            Long memberId = jwtUtil.getId(refreshToken);
            jwtUtil.invalidateRefreshToken(memberId);
        }
        jwtUtil.deleteRefreshTokenCookie(response);
    }

    @Transactional(readOnly = true)
    @Override
    public MemberResponseDTO.RefreshResultDTO reissueToken(String refreshToken) {
        // 토큰이 비어있거나 유효기간이 지났는지 검증
        if (refreshToken == null || !jwtUtil.isValidRefreshToken(refreshToken)) {
            throw new MemberException(MemberErrorCode.INVALID_TOKEN);
        }

        // 토큰 안에서 유저 ID 꺼내기
        Long memberId = jwtUtil.getId(refreshToken);

        // Redis에 저장된 진짜 토큰과 일치하는지 비교 (누군가 옛날 토큰으로 찌르는 것 방지)
        String savedToken = redisTemplate.opsForValue().get("refresh:token:" + memberId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new MemberException(MemberErrorCode.INVALID_TOKEN);
        }

        // 새 엑세스 토큰 발급을 위해 멤버 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 새 엑세스 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(member.getId(), member.getEmail(), member.getNickname());

        return MemberResponseDTO.RefreshResultDTO.builder()
                .accessToken(newAccessToken)
                .build();
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}