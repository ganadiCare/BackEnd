package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.Recording;

import java.util.List;
import java.util.Optional;

public interface RecordingRepository extends JpaRepository<Recording, Long> {

    List<Recording> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<Recording> findByIdAndMemberId(Long id, Long memberId);
}
