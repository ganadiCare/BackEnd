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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordingCommandServiceImpl implements RecordingCommandService {

    private final RecordingRepository recordingRepository;
    private final MemberRepository memberRepository;

    @Value("${recording.storage.path:/app/recordings}")
    private String storagePath;

    @Override
    public RecordingResponseDTO.UploadResultDTO upload(Long memberId, MultipartFile file, RecordingType type) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 저장 디렉토리 생성
        Path dir = Paths.get(storagePath, memberId.toString());
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new CameraException(CameraErrorCode.FILE_SAVE_FAILED);
        }

        // UUID로 저장 파일명 생성 (확장자 유지)
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : (type == RecordingType.VIDEO ? ".mp4" : ".jpg");
        String storedName = UUID.randomUUID() + ext;
        Path filePath = dir.resolve(storedName);

        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new CameraException(CameraErrorCode.FILE_SAVE_FAILED);
        }

        Recording recording = Recording.builder()
                .originalFileName(originalName)
                .storedFileName(storedName)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
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
    public void delete(Long memberId, Long recordingId) {
        Recording recording = recordingRepository.findByIdAndMemberId(recordingId, memberId)
                .orElseThrow(() -> new CameraException(CameraErrorCode.RECORDING_NOT_FOUND));

        // 파일 삭제
        try {
            Files.deleteIfExists(Paths.get(recording.getFilePath()));
        } catch (IOException e) {
            throw new CameraException(CameraErrorCode.FILE_DELETE_FAILED);
        }

        recordingRepository.delete(recording);
    }
}
