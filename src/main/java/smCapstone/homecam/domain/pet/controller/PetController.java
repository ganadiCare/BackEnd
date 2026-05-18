package smCapstone.homecam.domain.pet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import smCapstone.homecam.domain.pet.dto.request.PetRequestDTO;
import smCapstone.homecam.domain.pet.dto.response.PetResponseDTO;
import smCapstone.homecam.domain.pet.service.command.PetCommandService;
import smCapstone.homecam.domain.pet.service.query.PetQueryService;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pets")
@Tag(name = "Pet API", description = "반려동물 관리 API")
public class PetController {

    private final PetCommandService petCommandService;
    private final PetQueryService petQueryService;

    private Long getMemberId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping
    @Operation(summary = "내 반려동물 조회 API")
    public ApiResponse<PetResponseDTO.PetDTO> getMyPet() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, petQueryService.getMyPet(getMemberId()));
    }

    @PatchMapping
    @Operation(summary = "반려동물 정보 수정 API")
    public ApiResponse<PetResponseDTO.PetDTO> updatePet(
            @RequestBody PetRequestDTO.UpdatePetDTO request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, petCommandService.updatePet(getMemberId(), request));
    }

    @DeleteMapping
    @Operation(summary = "반려동물 삭제 API")
    public ApiResponse<String> deletePet() {
        petCommandService.deletePet(getMemberId());
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "반려동물이 삭제되었습니다.");
    }
}
