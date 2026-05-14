package smCapstone.homecam.domain.member.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import smCapstone.homecam.domain.pet.enums.PetGender;
import smCapstone.homecam.domain.pet.enums.PetSpecies;

import java.time.LocalDate;

public class MemberRequestDTO {

    public record SendCodeDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email
    ) {}

    public record VerifyCodeDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,
            @NotBlank(message = "인증번호를 입력해주세요.")
            String code
    ) {}

    public record SignUpDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,
            @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
            String password,
            @NotBlank(message = "닉네임을 입력해주세요.")
            String nickname,

            // @Valid를 붙여야 내부 필드(DeviceSettingDTO, PetSettingDTO) 검증이 동작합니다.
            @Valid DeviceSettingDTO device,
            @Valid PetSettingDTO pet
    ) {}

    public record DeviceSettingDTO(
            @NotBlank(message = "카메라 코드를 입력해주세요.")
            String cameraCode,
            @NotBlank(message = "디스펜서 코드를 입력해주세요.")
            String dispenserCode
    ) {}

    public record PetSettingDTO(
            @NotBlank(message = "반려동물 이름을 입력해주세요.")
            String name,
            @NotNull(message = "반려동물 종을 입력해주세요.")
            PetSpecies species,
            @NotNull(message = "반려동물 성별을 입력해주세요.")
            PetGender gender,
            @PositiveOrZero(message = "나이는 0 이상이어야 합니다.")
            Integer age,
            @PositiveOrZero(message = "몸무게는 0 이상이어야 합니다.")
            Double weight,
            @NotNull(message = "생일을 입력해주세요.") // LocalDate로 변경
            LocalDate birthday
    ) {}

    public record LoginDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            String email,
            @NotBlank(message = "비밀번호를 입력해주세요.")
            String password
    ) {}
}