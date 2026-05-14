package smCapstone.homecam.domain.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.device.entity.Camera;

public interface CameraRepository extends JpaRepository<Camera, Long> {
}
