package smCapstone.homecam.domain.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.pet.entity.Pet;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByMemberId(Long memberId);
}
