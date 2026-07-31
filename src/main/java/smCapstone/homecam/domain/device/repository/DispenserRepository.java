package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.Dispenser;

import java.util.Optional;

public interface DispenserRepository extends JpaRepository<Dispenser, Long> {
    Optional<Dispenser> findByMemberId(Long memberId);
    Optional<Dispenser> findFirstByOrderByIdAsc();
}
