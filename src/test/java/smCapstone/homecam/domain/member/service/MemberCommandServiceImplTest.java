package smCapstone.homecam.domain.member.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // 최신 표준 패키지 적용
import smCapstone.homecam.domain.device.entity.Camera;
import smCapstone.homecam.domain.device.entity.Dispenser;
import smCapstone.homecam.domain.device.enums.NightVision;
import smCapstone.homecam.domain.device.repository.CameraRepository;
import smCapstone.homecam.domain.device.repository.DispenserRepository;
import smCapstone.homecam.domain.device.repository.FeedingLogRepository;
import smCapstone.homecam.domain.device.repository.FeedingScheduleRepository;
import smCapstone.homecam.domain.device.repository.WateringLogRepository;
import smCapstone.homecam.domain.member.dto.request.MemberRequestDTO;
import smCapstone.homecam.domain.member.dto.response.MemberResponseDTO;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.member.exception.MemberErrorCode;
import smCapstone.homecam.domain.member.exception.MemberException;
import smCapstone.homecam.domain.member.repository.MemberRepository;
import smCapstone.homecam.domain.member.service.command.MemberCommandServiceImpl;
import smCapstone.homecam.domain.member.service.query.MemberQueryServiceImpl;
import smCapstone.homecam.domain.pet.entity.Pet;
import smCapstone.homecam.domain.pet.enums.PetGender;
import smCapstone.homecam.domain.pet.enums.PetSpecies;
import smCapstone.homecam.domain.pet.repository.PetRepository;
import smCapstone.homecam.domain.report.repository.ReportRepository;
import smCapstone.homecam.global.util.JwtUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "jwt.token.secretKey=this_is_a_temporary_secret_key_for_testing_purposes_only_32_bytes_long",
        "jwt.token.expiration.access=3600000",
        "jwt.token.expiration.refresh=1209600000",
        "JWT_SECRET_KEY=this_is_a_temporary_secret_key_for_testing_purposes_only_32_bytes_long",
        "GPT_API_KEY=dummy_gpt_key",
        "spring.mail.username=dummy_mail_user@gmail.com", // [추가] 메일 계정 더미값
        "spring.mail.password=dummy_mail_password"         // [추가] 메일 비밀번호 더미값
})
@Transactional
@DisplayName("MemberCommandService 통합 테스트")
public class MemberCommandServiceImplTest {

    // 1. 실제 테스트 타겟 및 영속성 레이어 주입
    @Autowired private MemberRepository memberRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private MemberCommandServiceImpl memberCommandService;
    @Autowired private MemberQueryServiceImpl memberQueryService;

    // 2. 외부 시스템 및 연관 리포지토리는 @MockitoBean을 사용하여 스프링 컨텍스트 내에서 가로챔
    @MockitoBean private StringRedisTemplate redisTemplate;
    @MockitoBean private JavaMailSender mailSender;
    @MockitoBean private CameraRepository cameraRepository;
    @MockitoBean private DispenserRepository dispenserRepository;
    @MockitoBean private FeedingLogRepository feedingLogRepository;
    @MockitoBean private FeedingScheduleRepository feedingScheduleRepository;
    @MockitoBean private WateringLogRepository wateringLogRepository;
    @MockitoBean private PetRepository petRepository;
    @MockitoBean private ReportRepository reportRepository;
    @MockitoBean private ValueOperations<String, String> valueOperations;

    private final Faker faker = new Faker(); // 인스턴스 변수 단계에서 상수로 선언

    @BeforeEach
    void setUp() {
        // [변경] MockitoAnnotations.openMocks(this) 제거
        // -> @MockitoBean을 쓰면 스프링이 구동될 때 Mock 객체를 생성 및 라이프사이클을 알아서 관리해 줍니다.

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(startsWith("VERIFIED:"))).thenReturn("TRUE");
        when(valueOperations.get(startsWith("AUTH:"))).thenReturn("123456");

        // 이 부분들은 원래 void 리턴이거나 이미 완벽하니 그대로 둡니다.
        doNothing().when(valueOperations).set(anyString(), anyString(), any(Duration.class));
        doNothing().when(valueOperations).set(anyString(), anyString());

        // [수정 완료] doNothing 대신 반환값을 지정해 줍니다.
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // 연관 엔티티 저장을 위한 Mock 스터빙
        when(cameraRepository.save(any(Camera.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(dispenserRepository.save(any(Dispenser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(cameraRepository).delete(any(Camera.class));
        doNothing().when(dispenserRepository).delete(any(Dispenser.class));
        doNothing().when(petRepository).delete(any(Pet.class));
        doNothing().when(feedingLogRepository).deleteAllByDispenserId(anyLong());
        doNothing().when(wateringLogRepository).deleteAllByDispenserId(anyLong());
        doNothing().when(feedingScheduleRepository).deleteAllByDispenserId(anyLong());
        doNothing().when(reportRepository).deleteAllByMemberId(anyLong());

        when(cameraRepository.findByMemberId(anyLong())).thenReturn(Optional.empty());
        when(dispenserRepository.findByMemberId(anyLong())).thenReturn(Optional.empty());
        when(petRepository.findByMemberId(anyLong())).thenReturn(Optional.empty());
    }

    private MemberRequestDTO.SignUpDTO createSignUpDTO() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(8, 20, true, true, true);
        String nickname = faker.name().firstName();

        MemberRequestDTO.DeviceSettingDTO deviceDTO = new MemberRequestDTO.DeviceSettingDTO(
                faker.code().asin(),
                faker.code().asin()
        );

        MemberRequestDTO.PetSettingDTO petDTO = new MemberRequestDTO.PetSettingDTO(
                faker.animal().name(),
                faker.options().option(PetSpecies.class),
                faker.options().option(PetGender.class),
                faker.number().numberBetween(1, 15),
                faker.number().randomDouble(2, 1, 30),
                LocalDate.of(faker.number().numberBetween(2010, 2023), faker.number().numberBetween(1, 12), faker.number().numberBetween(1, 28))
        );

        return new MemberRequestDTO.SignUpDTO(email, password, nickname, deviceDTO, petDTO);
    }

    private Member createTestMember(String email, String rawPassword, String nickname) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();
    }

    @Test
    @DisplayName("새로운 회원가입 성공")
    void signUp_Success() {
        // Given
        MemberRequestDTO.SignUpDTO signUpDTO = createSignUpDTO();

        // When
        MemberResponseDTO.SignUpResultDTO result = memberCommandService.signUp(signUpDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(signUpDTO.email());
        assertThat(result.nickname()).isEqualTo(signUpDTO.nickname());

        verify(redisTemplate, times(1)).delete("VERIFIED:" + signUpDTO.email());
        verify(petRepository, times(1)).save(any(Pet.class));
        verify(cameraRepository, times(1)).save(any(Camera.class));
        verify(dispenserRepository, times(1)).save(any(Dispenser.class));
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 시 예외 발생")
    void signUp_DuplicateEmail_ThrowsException() {
        // Given
        MemberRequestDTO.SignUpDTO signUpDTO = createSignUpDTO();

        // 실제 데이터베이스(memberRepository)에 기존 회원을 미리 저장하여 자연스럽게 중복 상황을 유도
        Member existingMember = createTestMember(signUpDTO.email(), "password123", "duplicate");
        memberRepository.save(existingMember);

        // When & Then
        MemberException exception = assertThrows(MemberException.class, () -> memberCommandService.signUp(signUpDTO));
        assertThat(exception.getCode()).isEqualTo(MemberErrorCode.MEMBER_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("이메일 미인증 상태에서 회원가입 시 예외 발생")
    void signUp_EmailNotVerified_ThrowsException() {
        // Given
        MemberRequestDTO.SignUpDTO signUpDTO = createSignUpDTO();
        when(valueOperations.get("VERIFIED:" + signUpDTO.email())).thenReturn(null); // 특정 이메일에 대해 미인증 처리

        // When & Then
        MemberException exception = assertThrows(MemberException.class, () -> memberCommandService.signUp(signUpDTO));
        assertThat(exception.getCode()).isEqualTo(MemberErrorCode.EMAIL_NOT_VERIFIED);
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // Given
        String email = faker.internet().emailAddress();
        String rawPassword = faker.internet().password(8, 20, true, true, true);
        Member existingMember = createTestMember(email, rawPassword, faker.name().firstName());
        memberRepository.save(existingMember);

        MemberRequestDTO.LoginDTO loginDTO = new MemberRequestDTO.LoginDTO(email, rawPassword);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // When
        MemberResponseDTO.LoginResultDTO result = memberCommandService.login(loginDTO, response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.memberId()).isEqualTo(existingMember.getId());
        assertThat(result.accessToken()).isNotBlank();
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void login_MemberNotFound_ThrowsException() {
        // Given
        MemberRequestDTO.LoginDTO loginDTO = new MemberRequestDTO.LoginDTO(faker.internet().emailAddress(), faker.internet().password());
        HttpServletResponse response = mock(HttpServletResponse.class);

        // When & Then
        MemberException exception = assertThrows(MemberException.class, () -> memberCommandService.login(loginDTO, response));
        assertThat(exception.getCode()).isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_InvalidPassword_ThrowsException() {
        // Given
        String email = faker.internet().emailAddress();
        String rawPassword = faker.internet().password(8, 20, true, true, true);
        Member existingMember = createTestMember(email, rawPassword, faker.name().firstName());
        memberRepository.save(existingMember);

        MemberRequestDTO.LoginDTO loginDTO = new MemberRequestDTO.LoginDTO(email, "wrongpassword");
        HttpServletResponse response = mock(HttpServletResponse.class);

        // When & Then
        MemberException exception = assertThrows(MemberException.class, () -> memberCommandService.login(loginDTO, response));
        assertThat(exception.getCode()).isEqualTo(MemberErrorCode.INVALID_PASSWORD);
    }

    @Test
    @DisplayName("프로필 조회 성공")
    void getMyProfile_Success() {
        // Given
        String email = faker.internet().emailAddress();
        String rawPassword = faker.internet().password(8, 20, true, true, true);
        String nickname = faker.name().firstName();
        Member existingMember = createTestMember(email, rawPassword, nickname);
        memberRepository.save(existingMember);

        Pet existingPet = Pet.builder()
                .name(faker.animal().name())
                .species(PetSpecies.DOG)
                .gender(PetGender.FEMALE)
                .age(5)
                .weight(10.5)
                .birthday(LocalDate.now().minusYears(5))
                .member(existingMember)
                .build();
        when(petRepository.findByMemberId(existingMember.getId())).thenReturn(Optional.of(existingPet));

        Camera existingCamera = Camera.builder()
                .deviceCode(faker.code().asin())
                .nightVision(NightVision.AUTO)
                .isPrivateMode(false)
                .isAutoRecordMode(true)
                .member(existingMember)
                .build();
        when(cameraRepository.findByMemberId(existingMember.getId())).thenReturn(Optional.of(existingCamera));

        // When
        MemberResponseDTO.ProfileDTO profile = memberQueryService.getMyProfile(existingMember.getId());

        // Then
        assertThat(profile).isNotNull();
        assertThat(profile.nickname()).isEqualTo(nickname);
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.pet().name()).isEqualTo(existingPet.getName());
        assertThat(profile.device().cameraCode()).isEqualTo(existingCamera.getDeviceCode());
    }

    @Test
    @DisplayName("계정 삭제 성공")
    void deleteMember_Success() {
        // Given
        String email = faker.internet().emailAddress();
        String rawPassword = faker.internet().password(8, 20, true, true, true);
        Member existingMember = createTestMember(email, rawPassword, faker.name().firstName());
        memberRepository.save(existingMember);

        Long memberId = existingMember.getId();
        Dispenser mockDispenser = Dispenser.builder().id(faker.number().randomNumber()).deviceCode(faker.code().asin()).member(existingMember).build();
        Camera mockCamera = Camera.builder().id(faker.number().randomNumber()).deviceCode(faker.code().asin()).nightVision(NightVision.AUTO).member(existingMember).build();
        Pet mockPet = Pet.builder().id(faker.number().randomNumber()).name(faker.animal().name()).species(PetSpecies.DOG).member(existingMember).build();

        when(dispenserRepository.findByMemberId(memberId)).thenReturn(Optional.of(mockDispenser));
        when(cameraRepository.findByMemberId(memberId)).thenReturn(Optional.of(mockCamera));
        when(petRepository.findByMemberId(memberId)).thenReturn(Optional.of(mockPet));

        HttpServletResponse response = mock(HttpServletResponse.class);

        // When
        memberCommandService.deleteMember(memberId, response);

        // Then
        Optional<Member> foundMember = memberRepository.findById(memberId);
        assertThat(foundMember).isNotPresent();

        verify(dispenserRepository, times(1)).delete(any(Dispenser.class));
        verify(cameraRepository, times(1)).delete(any(Camera.class));
        verify(petRepository, times(1)).delete(any(Pet.class));
        verify(feedingLogRepository, times(1)).deleteAllByDispenserId(eq(mockDispenser.getId()));
        verify(reportRepository, times(1)).deleteAllByMemberId(eq(memberId));
    }
}