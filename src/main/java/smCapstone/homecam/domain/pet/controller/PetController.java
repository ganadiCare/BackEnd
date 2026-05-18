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

import java.util.List;

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
    @Operation(summary = "내 반려동물 목록 조회 API")
    public ApiResponse<List<PetResponseDTO.PetDTO>> getMyPets() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, petQueryService.getMyPets(getMemberId()));
    }

    @GetMapping("/{petId}")
    @Operation(summary = "반려동물 단건 조회 API")
    public ApiResponse<PetResponseDTO.PetDTO> getPet(@PathVariable Long petId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, petQueryService.getPet(getMemberId(), petId));
    }

    @PatchMapping("/{petId}")
    @Operation(summary = "반려동물 정보 수정 API")
    public ApiResponse<PetResponseDTO.PetDTO> updatePet(
            @PathVariable Long petId,
            @RequestBody PetRequestDTO.UpdatePetDTO request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, petCommandService.updatePet(getMemberId(), petId, request));
    }

    @DeleteMapping("/{petId}")
    @Operation(summary = "반려동물 삭제 API")
    public ApiResponse<String> deletePet(@PathVariable Long petId) {
        petCommandService.deletePet(getMemberId(), petId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, "반려동물이 삭제되었습니다.");
    }
}
