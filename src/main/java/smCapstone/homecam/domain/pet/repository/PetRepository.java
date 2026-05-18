package smCapstone.homecam.domain.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.pet.entity.Pet;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findAllByMemberId(Long memberId);
}
