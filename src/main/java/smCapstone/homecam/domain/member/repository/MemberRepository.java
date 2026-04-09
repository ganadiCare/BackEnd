package smCapstone.homecam.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
