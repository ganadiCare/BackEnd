package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.Dispenser;

public interface DispenserRepository extends JpaRepository<Dispenser, Long> {
}
