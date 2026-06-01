package smCapstone.homecam.domain.device.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smCapstone.homecam.domain.device.dto.response.RecordingResponseDTO;
import smCapstone.homecam.domain.device.entity.Recording;
import smCapstone.homecam.domain.device.exception.CameraErrorCode;
import smCapstone.homecam.domain.device.exception.CameraException;
import smCapstone.homecam.domain.device.repository.RecordingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordingQueryServiceImpl implements RecordingQueryService {

    private final RecordingRepository recordingRepository;

    @Override
    public RecordingResponseDTO.RecordingListDTO getList(Long memberId) {
        List<Recording> recordings = recordingRepository.findByMemberIdOrderByCreatedAtDesc(memberId);

        List<RecordingResponseDTO.RecordingItemDTO> items = recordings.stream()
                .map(r -> RecordingResponseDTO.RecordingItemDTO.builder()
                        .id(r.getId())
                        .originalFileName(r.getOriginalFileName())
                        .type(r.getType())
                        .fileSize(r.getFileSize())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();

        return RecordingResponseDTO.RecordingListDTO.builder()
                .recordings(items)
                .build();
    }

    @Override
    public Recording getRecordingFile(Long memberId, Long recordingId) {
        return recordingRepository.findByIdAndMemberId(recordingId, memberId)
                .orElseThrow(() -> new CameraException(CameraErrorCode.RECORDING_NOT_FOUND));
    }
}
