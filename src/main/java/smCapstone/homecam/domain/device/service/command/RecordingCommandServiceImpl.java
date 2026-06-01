package smCapstone.homecam.domain.device.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import smCapstone.homecam.domain.device.dto.response.RecordingResponseDTO;
import smCapstone.homecam.domain.device.entity.Recording;
import smCapstone.homecam.domain.device.enums.RecordingType;
import smCapstone.homecam.domain.device.exception.CameraErrorCode;
import smCapstone.homecam.domain.device.exception.CameraException;
import smCapstone.homecam.domain.device.repository.RecordingRepository;
import smCapstone.homecam.domain.member.entity.Member;
import smCapstone.homecam.domain.member.exception.MemberErrorCode;
import smCapstone.homecam.domain.member.exception.MemberException;
import smCapstone.homecam.domain.member.repository.MemberRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordingCommandServiceImpl implements RecordingCommandService {

    private final RecordingRepository recordingRepository;
    private final MemberRepository memberRepository;

    @Value("${recording.storage.path:/app/recordings}")
    private String storagePath;

    // 허용 확장자 화이트리스트
    private static final Set<String> ALLOWED_VIDEO_EXT = Set.of(".mp4", ".mov", ".avi");
    private static final Set<String> ALLOWED_IMAGE_EXT = Set.of(".jpg", ".jpeg", ".png");

    @Override
    public RecordingResponseDTO.UploadResultDTO upload(Long memberId, MultipartFile file, RecordingType type) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 확장자 화이트리스트 검증
        String ext = resolveExtension(file.getOriginalFilename(), type);

        // 저장 디렉토리 생성
        Path dir = Paths.get(storagePath, memberId.toString());
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new CameraException(CameraErrorCode.FILE_SAVE_FAILED);
        }

        String storedName = UUID.randomUUID() + ext;

        // 경로 조작 방지: normalize 후 dir 하위인지 검증
        Path filePath = dir.resolve(storedName).normalize();
        if (!filePath.startsWith(dir.normalize())) {
            throw new CameraException(CameraErrorCode.FILE_SAVE_FAILED);
        }

        // ① 파일을 디스크에 저장 (트랜잭션 밖)
        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new CameraException(CameraErrorCode.FILE_SAVE_FAILED);
        }

        // ② DB 저장 — 실패 시 디스크 파일 정리
        try {
            return saveRecording(member, file.getOriginalFilename(), storedName, filePath, file.getSize(), type);
        } catch (Exception e) {
            // 고아 파일 방지: DB 저장 실패 시 디스크 파일 삭제
            try { Files.deleteIfExists(filePath); } catch (IOException ignored) {}
            throw e;
        }
    }

    @Transactional
    protected RecordingResponseDTO.UploadResultDTO saveRecording(
            Member member, String originalName, String storedName,
            Path filePath, long fileSize, RecordingType type) {

        Recording recording = Recording.builder()
                .originalFileName(originalName)
                .storedFileName(storedName)
                .filePath(filePath.toString())
                .fileSize(fileSize)
                .type(type)
                .member(member)
                .build();

        recordingRepository.save(recording);

        return RecordingResponseDTO.UploadResultDTO.builder()
                .id(recording.getId())
                .originalFileName(recording.getOriginalFileName())
                .type(recording.getType())
                .fileSize(recording.getFileSize())
                .createdAt(recording.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void delete(Long memberId, Long recordingId) {
        Recording recording = recordingRepository.findByIdAndMemberId(recordingId, memberId)
                .orElseThrow(() -> new CameraException(CameraErrorCode.RECORDING_NOT_FOUND));

        try {
            Files.deleteIfExists(Paths.get(recording.getFilePath()));
        } catch (IOException e) {
            throw new CameraException(CameraErrorCode.FILE_DELETE_FAILED);
        }

        recordingRepository.delete(recording);
    }

    private String resolveExtension(String originalName, RecordingType type) {
        Set<String> allowed = type == RecordingType.VIDEO ? ALLOWED_VIDEO_EXT : ALLOWED_IMAGE_EXT;
        String defaultExt = type == RecordingType.VIDEO ? ".mp4" : ".jpg";

        if (originalName == null || !originalName.contains(".")) return defaultExt;

        String ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        if (!allowed.contains(ext)) {
            throw new CameraException(CameraErrorCode.FILE_SAVE_FAILED);
        }
        return ext;
    }
}
