package smCapstone.homecam.domain.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.pet.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
