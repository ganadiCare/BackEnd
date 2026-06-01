package smCapstone.homecam.domain.device.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import smCapstone.homecam.domain.device.dto.response.RecordingResponseDTO;
import smCapstone.homecam.domain.device.entity.Recording;
import smCapstone.homecam.domain.device.enums.RecordingType;
import smCapstone.homecam.domain.device.service.command.RecordingCommandService;
import smCapstone.homecam.domain.device.service.query.RecordingQueryService;
import smCapstone.homecam.global.apipayload.GeneralSuccessCode;
import smCapstone.homecam.global.exception.ApiResponse;
import smCapstone.homecam.global.util.SecurityUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recordings")
@Tag(name = "Recording API", description = "녹화/캡처 파일 관리 API (라즈베리파이 연동)")
public class RecordingController {

    private final RecordingCommandService recordingCommandService;
    private final RecordingQueryService recordingQueryService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드", description = "라즈베리파이에서 녹화/캡처 파일을 업로드합니다.")
    public ApiResponse<RecordingResponseDTO.UploadResultDTO> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam RecordingType type
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED,
                recordingCommandService.upload(SecurityUtil.getCurrentMemberId(), file, type));
    }

    @GetMapping
    @Operation(summary = "녹화/캡처 목록 조회")
    public ApiResponse<RecordingResponseDTO.RecordingListDTO> getList() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                recordingQueryService.getList(SecurityUtil.getCurrentMemberId()));
    }

    @GetMapping("/{recordingId}/download")
    @Operation(summary = "파일 다운로드/스트리밍")
    public ResponseEntity<Resource> download(@PathVariable Long recordingId) {
        Recording recording = recordingQueryService.getRecordingFile(
                SecurityUtil.getCurrentMemberId(), recordingId);

        Resource resource = new FileSystemResource(Paths.get(recording.getFilePath()));

        // 파일 타입에 따라 Content-Type 설정
        MediaType mediaType = recording.getType() == RecordingType.VIDEO
                ? MediaType.parseMediaType("video/mp4")
                : MediaType.IMAGE_JPEG;

        String encodedName = URLEncoder.encode(recording.getOriginalFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(resource);
    }

    @DeleteMapping("/{recordingId}")
    @Operation(summary = "파일 삭제")
    public ApiResponse<Void> delete(@PathVariable Long recordingId) {
        recordingCommandService.delete(SecurityUtil.getCurrentMemberId(), recordingId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }
}
