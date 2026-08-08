package smCapstone.homecam.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smCapstone.homecam.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findFirstByOrderByIdAsc();
    Optional<Member> findByEmail(String email); //이메일로 회원 찾기
    boolean existsByEmail(String email); // 이메일 중복 등록 확인
}
